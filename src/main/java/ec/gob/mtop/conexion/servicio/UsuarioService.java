package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.repositorio.UsuarioRepository;
import ec.gob.mtop.conexion.seguridad.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Lazy
    @Autowired
    private UserDetailsService userDetailsService; // 🔹 Se usa para validar tokens

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void inicializarAdmin() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombreUsuario("Administrador");
            admin.setUsernameUsuario("admin");
            admin.setPasswordUsuario(passwordEncoder.encode("admin123")); // Cifrado de contraseña
            admin.setRolUsuario("ADMIN");
            admin.setRegistroActivo(true);
            admin.setCreadoPorUsuario((short) 1); // Se asigna a sí mismo
            admin.setFechaCreacion(LocalDateTime.now());

            usuarioRepository.save(admin);
            System.out.println("🔹 Usuario admin creado automáticamente");
        }
    }

    // 🔹 Obtener todos los usuarios activos
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findByRegistroActivoTrue();
    }

    // 🔹 Obtener usuario por ID
    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id).filter(Usuario::getRegistroActivo);
    }

    // 🔹 Obtener usuario por username
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsernameUsuarioAndRegistroActivoTrue(username);
    }

    // 🔹 Crear un usuario (cifrado de contraseña)
    public Usuario guardarUsuario(Usuario usuario, Short creadoPor) {
        usuario.setCreadoPorUsuario(creadoPor);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setRegistroActivo(true);
        usuario.setPasswordUsuario(passwordEncoder.encode(usuario.getPasswordUsuario())); // 🔹 Cifrar contraseña antes de guardar
        return usuarioRepository.save(usuario);
    }

    // 🔹 Actualizar usuario (incluye cifrado de nueva contraseña)
    public Usuario actualizarUsuario(Integer id, Usuario usuarioDetalles, Short modificadoPor) {
        return usuarioRepository.findById(id)
                .filter(Usuario::getRegistroActivo)
                .map(usuario -> {
                    usuario.setNombreUsuario(usuarioDetalles.getNombreUsuario());
                    usuario.setUsernameUsuario(usuarioDetalles.getUsernameUsuario());
                    usuario.setPasswordUsuario(passwordEncoder.encode(usuarioDetalles.getPasswordUsuario())); // 🔹 Cifrar la nueva contraseña
                    usuario.setRolUsuario(usuarioDetalles.getRolUsuario());
                    usuario.setModificadoPorUsuario(modificadoPor);
                    usuario.setFechaModificacion(LocalDateTime.now());
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo con ID: " + id));
    }

    // 🔹 Borrado lógico del usuario
    public void eliminarUsuario(Integer id, Short modificadoPor) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setRegistroActivo(false);
            usuario.setModificadoPorUsuario(modificadoPor);
            usuario.setFechaModificacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    // 🔹 Validar credenciales (usuario y contraseña)
    public boolean validarCredenciales(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameUsuarioAndRegistroActivoTrue(username);
        return usuarioOpt.isPresent() && passwordEncoder.matches(password, usuarioOpt.get().getPasswordUsuario());
    }

    // 🔹 Generar un token JWT
    public String generarToken(String username) {
        return jwtUtil.generarToken(username);
    }

    // 🔹 Validar un token JWT con el usuario correcto
    public boolean validarToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token); // Extrae el usuario del token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Carga el usuario
            return jwtUtil.validarToken(token, userDetails); // Valida el token contra el usuario
        } catch (Exception e) {
            return false; // Si hay algún error, el token no es válido
        }
    }
}

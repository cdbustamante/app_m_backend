package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.servicio.UsuarioService;
import ec.gob.mtop.conexion.seguridad.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")

public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 Obtener todos los usuarios activos
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // 🔹 Obtener usuario por ID (solo si está activo)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Buscar usuario por username
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> obtenerPorUsername(@RequestParam String username) {
        return usuarioService.obtenerPorUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Crear un usuario (debe incluir el ID del creador)
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario, @RequestParam Short creadoPor) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario, creadoPor);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // 🔹 Actualizar usuario (requiere el ID del usuario que hace la modificación)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Integer id,
            @RequestBody Usuario usuarioDetalles,
            @RequestParam Short modificadoPor) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, usuarioDetalles, modificadoPor);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Borrado lógico del usuario (requiere el ID del usuario que lo modifica)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id, @RequestParam Short modificadoPor) {
        usuarioService.eliminarUsuario(id, modificadoPor);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Endpoint para iniciar sesión y generar token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (usuarioService.validarCredenciales(request.getUsername(), request.getPassword())) {
            System.out.println("✅ Usuario autenticado correctamente: " + request.getUsername());
            String token = usuarioService.generarToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        System.out.println("❌ Fallo en la autenticación para usuario: " + request.getUsername());
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    // 🔹 Obtener perfil del usuario autenticado
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        // 📌 Obtener el usuario autenticado desde el token
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        System.out.println("🔍 Intentando obtener perfil para usuario: " + username);
    
        Optional<Usuario> usuario = usuarioService.obtenerPorUsername(username);
    
        if (usuario.isPresent()) {
            Usuario user = usuario.get();
            System.out.println("✅ Usuario encontrado: " + user.getUsernameUsuario() + " con rol: " + user.getRolUsuario());
            return ResponseEntity.ok(new PerfilResponse(user.getId(), user.getNombreUsuario(), user.getRolUsuario()));
        } else {
            System.out.println("❌ Usuario no encontrado en la base de datos");
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

}

// Clase auxiliar para la solicitud de inicio de sesión
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

// Clase auxiliar para la respuesta con el token JWT
class AuthResponse {
    private String token;

    public AuthResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}
// Clase auxiliar para devolver el perfil del usuario autenticado
class PerfilResponse {
    private Integer id;
    private String username;
    private String rol;

    public PerfilResponse(Integer id, String username, String rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getRol() { return rol; }
}


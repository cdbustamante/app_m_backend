package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Buscar un Usuario por ID si está activo
     */
    public Usuario buscarPorId(Short id) {
        try {
            return usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el usuario: " + e.getMessage());
        }
    }

    /**
     * Buscar un Usuario por su nombre de usuario si está activo (para autenticación)
     */
    public Usuario buscarPorUsername(String username) {
        try {
            return usuarioRepository.findByUsernameUsuarioAndRegistroActivoTrue(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        } catch (Exception e) {
            throw new GetException("Error al obtener el usuario por username: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Usuarios activos
     */
    public List<Usuario> listarTodos() {
        try {
            return usuarioRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los usuarios: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Usuario, asignando el usuario creador
     */
    public Usuario crear(Usuario usuario, Short idUsuario) {
        try {
            usuario.setNombreUsuario(usuario.getNombreUsuario());
            usuario.setUsernameUsuario(usuario.getUsernameUsuario());
            usuario.setPasswordUsuario(usuario.getPasswordUsuario());
            usuario.setRolUsuario(usuario.getRolUsuario());
            usuario.setRegistroActivo(true);
            usuario.setCreadoPorUsuario(idUsuario);
            usuario.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            usuario.setModificadoPorUsuario(null);
            usuario.setFechaModificacion(null);

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new InsertException("Error al insertar el usuario: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Usuario existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return usuarioRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del usuario: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Usuario (desactiva el anterior y crea uno nuevo)
     */
    public Usuario actualizar(Short id, Usuario nuevosDatos, Short idUsuario) {
        try {
            Usuario usuarioExistente = usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            usuarioExistente.setRegistroActivo(false);
            usuarioExistente.setModificadoPorUsuario(idUsuario);
            usuarioExistente.setFechaModificacion(LocalDateTime.now());
            usuarioRepository.save(usuarioExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nuevosDatos.getNombreUsuario());
            nuevoUsuario.setUsernameUsuario(nuevosDatos.getUsernameUsuario());
            nuevoUsuario.setPasswordUsuario(nuevosDatos.getPasswordUsuario());
            nuevoUsuario.setRolUsuario(nuevosDatos.getRolUsuario());
            nuevoUsuario.setRegistroActivo(true);
            nuevoUsuario.setCreadoPorUsuario(idUsuario);
            nuevoUsuario.setFechaCreacion(LocalDateTime.now());

            return usuarioRepository.save(nuevoUsuario);

        } catch (ResourceNotFoundException e) {
            throw e; // Se lanza directamente si no se encuentra el registro
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Usuario (cambia estado a inactivo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

            usuario.setRegistroActivo(false);
            usuario.setModificadoPorUsuario(idUsuario);
            usuario.setFechaModificacion(LocalDateTime.now());
            usuarioRepository.save(usuario);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el usuario: " + e.getMessage());
        }
    }
}

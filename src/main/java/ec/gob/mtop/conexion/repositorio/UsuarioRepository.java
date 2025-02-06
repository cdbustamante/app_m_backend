package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Short> {

    // Retorna solo los Usuarios activos
    List<Usuario> findByRegistroActivoTrue();

    // Verifica si existe un Usuario activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);

    // Buscar usuario por username (para autenticación)
    Optional<Usuario> findByUsernameUsuarioAndRegistroActivoTrue(String username);
    
    List<Usuario> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);
    

}

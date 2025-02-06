package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // 🔹 Obtiene solo los usuarios activos
    List<Usuario> findByRegistroActivoTrue();

    // 🔹 Busca un usuario por username si está activo
    Optional<Usuario> findByUsernameUsuarioAndRegistroActivoTrue(String usernameUsuario);
}

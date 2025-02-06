package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Rack;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackRepository extends JpaRepository<Rack, Short> {
	// Retorna solo los racks activos
    List<Rack> findByRegistroActivoTrue();

    // Verifica si existe un rack activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Rack> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);

}

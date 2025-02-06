package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Rack;
import ec.gob.mtop.conexion.modelo.Switch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwitchRepository extends JpaRepository<Switch, Short> {
	// Retorna solo los switches activos
    List<Switch> findByRegistroActivoTrue();

    // Verifica si existe un switch activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Switch> findByRack(Rack rack);
    
    List<Switch> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);
    

}


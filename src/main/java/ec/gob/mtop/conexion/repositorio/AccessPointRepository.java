package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.AccessPoint;
import ec.gob.mtop.conexion.modelo.Puerto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessPointRepository extends JpaRepository<AccessPoint, Short> {
	// Retorna solo los Access Points activos
    List<AccessPoint> findByRegistroActivoTrue();

    // Verifica si existe un Access Point activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<AccessPoint> findByPuerto(Puerto puerto);
    
    List<AccessPoint> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);
}

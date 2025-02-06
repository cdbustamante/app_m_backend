package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.VlanSwitch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VlanSwitchRepository extends JpaRepository<VlanSwitch, Short> {
	// Retorna solo las VLANs activas
    List<VlanSwitch> findByRegistroActivoTrue();

    // Verifica si existe una VLAN activa con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<VlanSwitch> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);

}


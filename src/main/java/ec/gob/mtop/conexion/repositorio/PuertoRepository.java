package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.modelo.VlanSwitch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuertoRepository extends JpaRepository<Puerto, Short> {
	// Retorna solo los puertos activos
    List<Puerto> findByRegistroActivoTrue();

    // Verifica si existe un puerto activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Puerto> findBySwitchRed(Switch switchRed);
    
    List<Puerto> findByVlanSwitch(VlanSwitch vlanSwitch);
    
    List<Puerto> findByCreadoPorUsuarioOrModificadoPorUsuario(Short idUsuario, Short idUsuarioMod);


}

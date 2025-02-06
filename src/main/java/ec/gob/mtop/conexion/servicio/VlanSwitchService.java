package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.VlanSwitch;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import ec.gob.mtop.conexion.repositorio.VlanSwitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VlanSwitchService {

    @Autowired
    private VlanSwitchRepository vlanSwitchRepository;
    
    @Autowired
    private PuertoRepository puertoRepository;

    /**
     * Buscar una VLAN por ID si está activa
     */
    public VlanSwitch buscarPorId(Short id) {
        try {
            return vlanSwitchRepository.findById(id)
                    .filter(VlanSwitch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("VLAN no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener la VLAN: " + e.getMessage());
        }
    }

    /**
     * Listar todas las VLANs activas
     */
    public List<VlanSwitch> listarTodos() {
        try {
            return vlanSwitchRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar las VLANs: " + e.getMessage());
        }
    }

    /**
     * Crear una nueva VLAN, asignando el usuario creador
     */
    public VlanSwitch crear(VlanSwitch vlan, Short idUsuario) {
        try {
            vlan.setNombreVlan(vlan.getNombreVlan());
            vlan.setRegistroActivo(true);
            vlan.setCreadoPorUsuario(idUsuario);
            vlan.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            vlan.setModificadoPorUsuario(null);
            vlan.setFechaModificacion(null);

            return vlanSwitchRepository.save(vlan);
        } catch (Exception e) {
            throw new InsertException("Error al insertar la VLAN: " + e.getMessage());
        }
    }

    /**
     * Verificar si una VLAN existe y está activa
     */
    public boolean existePorId(Short id) {
        try {
            return vlanSwitchRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia de la VLAN: " + e.getMessage());
        }
    }

    /**
     * Actualizar una VLAN Switch (Desactiva la anterior, crea una nueva y actualiza referencias en Puerto)
     */
    public VlanSwitch actualizar(Short id, VlanSwitch nuevosDatos, Short idUsuario) {
        try {
            VlanSwitch vlanSwitchExistente = vlanSwitchRepository.findById(id)
                    .filter(VlanSwitch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("VLAN Switch no encontrada con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            vlanSwitchExistente.setRegistroActivo(false);
            vlanSwitchExistente.setModificadoPorUsuario(idUsuario);
            vlanSwitchExistente.setFechaModificacion(LocalDateTime.now());
            vlanSwitchRepository.save(vlanSwitchExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            VlanSwitch nuevaVlanSwitch = new VlanSwitch();
            nuevaVlanSwitch.setNombreVlan(nuevosDatos.getNombreVlan());
            nuevaVlanSwitch.setRegistroActivo(true);
            nuevaVlanSwitch.setCreadoPorUsuario(idUsuario);
            nuevaVlanSwitch.setFechaCreacion(LocalDateTime.now());

            // Guardar la nueva VLAN Switch
            VlanSwitch vlanSwitchGuardada = vlanSwitchRepository.save(nuevaVlanSwitch);

            // 3️⃣ **Actualizar todos los Puertos que referenciaban la VLAN Switch anterior**
            List<Puerto> puertos = puertoRepository.findByVlanSwitch(vlanSwitchExistente);
            for (Puerto puerto : puertos) {
                puerto.setVlanSwitch(vlanSwitchGuardada);
            }
            puertoRepository.saveAll(puertos);

            return vlanSwitchGuardada;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar la VLAN Switch: " + e.getMessage());
        }
    }

    /**
     * Eliminar una VLAN Switch (Cambia estado a inactivo y deja nulos los FK en Puerto)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            VlanSwitch vlanSwitch = vlanSwitchRepository.findById(id)
                    .filter(VlanSwitch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("VLAN Switch no encontrada con ID: " + id));

            // 1️⃣ **Actualizar todos los Puertos que referenciaban esta VLAN Switch a NULL**
            List<Puerto> puertos = puertoRepository.findByVlanSwitch(vlanSwitch);
            for (Puerto puerto : puertos) {
                
                puerto.setRegistroActivo(false);
                puerto.setModificadoPorUsuario(idUsuario);
                puerto.setFechaModificacion(LocalDateTime.now());
                puertoRepository.save(puerto);

            }
            puertoRepository.saveAll(puertos);

            // 2️⃣ Desactivar la VLAN Switch
            vlanSwitch.setRegistroActivo(false);
            vlanSwitch.setModificadoPorUsuario(idUsuario);
            vlanSwitch.setFechaModificacion(LocalDateTime.now());
            vlanSwitchRepository.save(vlanSwitch);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar la VLAN Switch: " + e.getMessage());
        }
    }
}

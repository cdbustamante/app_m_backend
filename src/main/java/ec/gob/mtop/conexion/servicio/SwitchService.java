package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.Rack;
import ec.gob.mtop.conexion.repositorio.SwitchRepository;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import ec.gob.mtop.conexion.repositorio.RackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SwitchService {

    @Autowired
    private SwitchRepository switchRepository;

    @Autowired
    private RackRepository rackRepository;
    
    @Autowired
    private PuertoRepository puertoRepository;

    /**
     * Buscar un Switch por ID si está activo
     */
    public Switch buscarPorId(Short id) {
        try {
            return switchRepository.findById(id)
                    .filter(Switch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Switch no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el switch: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Switches activos
     */
    public List<Switch> listarTodos() {
        try {
            return switchRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los switches: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Switch, asignando el usuario creador y validando el Rack
     */
    public Switch crear(Switch sw, Short idUsuario, Short idRack) {
        try {
            // Verificar si el Rack existe
            Rack rack = rackRepository.findById(idRack)
                    .orElseThrow(() -> new ResourceNotFoundException("Rack no encontrado con ID: " + idRack));

            sw.setNombreSw(sw.getNombreSw());
            sw.setModeloSw(sw.getModeloSw());
            sw.setMacEthSw(sw.getMacEthSw());
            sw.setIpSw(sw.getIpSw());
            sw.setRack(rack);
            sw.setRegistroActivo(true);
            sw.setCreadoPorUsuario(idUsuario);
            sw.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            sw.setModificadoPorUsuario(null);
            sw.setFechaModificacion(null);

            return switchRepository.save(sw);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar el switch: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Switch existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return switchRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del switch: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Switch (Desactiva el anterior, crea uno nuevo y actualiza referencias en Puerto)
     */
    public Switch actualizar(Short id, Switch nuevosDatos, Short idUsuario) {
        try {
            Switch switchExistente = switchRepository.findById(id)
                    .filter(Switch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Switch no encontrado con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            switchExistente.setRegistroActivo(false);
            switchExistente.setModificadoPorUsuario(idUsuario);
            switchExistente.setFechaModificacion(LocalDateTime.now());
            switchRepository.save(switchExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Switch nuevoSwitch = new Switch();
            nuevoSwitch.setNombreSw(nuevosDatos.getNombreSw());
            nuevoSwitch.setModeloSw(nuevosDatos.getModeloSw());
            nuevoSwitch.setMacEthSw(nuevosDatos.getMacEthSw());
            nuevoSwitch.setIpSw(nuevosDatos.getIpSw());
            nuevoSwitch.setRegistroActivo(true);
            nuevoSwitch.setCreadoPorUsuario(idUsuario);
            nuevoSwitch.setFechaCreacion(LocalDateTime.now());

            // Guardar el nuevo switch
            Switch switchGuardado = switchRepository.save(nuevoSwitch);

            // 3️⃣ **Actualizar todos los Puertos que referenciaban el Switch anterior**
            List<Puerto> puertos = puertoRepository.findBySwitchRed(switchExistente);
            for (Puerto puerto : puertos) {
                puerto.setSwitchRed(switchGuardado);
            }
            puertoRepository.saveAll(puertos);

            return switchGuardado;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el switch: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Switch (Cambia estado a inactivo y deja nulos los FK en Puerto)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Switch switchRed = switchRepository.findById(id)
                    .filter(Switch::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Switch no encontrado con ID: " + id));

            // 1️⃣ **Actualizar todos los Puertos que referenciaban este Switch a NULL**
            List<Puerto> puertos = puertoRepository.findBySwitchRed(switchRed);
            for (Puerto puerto : puertos) {
                puerto.setSwitchRed(null);
            }
            puertoRepository.saveAll(puertos);

            // 2️⃣ Desactivar el Switch
            switchRed.setRegistroActivo(false);
            switchRed.setModificadoPorUsuario(idUsuario);
            switchRed.setFechaModificacion(LocalDateTime.now());
            switchRepository.save(switchRed);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el switch: " + e.getMessage());
        }
    }
}

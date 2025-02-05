package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Rack;
import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.repositorio.RackRepository;
import ec.gob.mtop.conexion.repositorio.SwitchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RackService {

    @Autowired
    private RackRepository rackRepository;
    
    @Autowired
    private SwitchRepository switchRepository;

    /**
     * Buscar un Rack por ID si está activo
     */
    public Rack buscarPorId(Short id) {
        try {
            return rackRepository.findById(id)
                    .filter(Rack::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Rack no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el rack: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Racks activos
     */
    public List<Rack> listarTodos() {
        try {
            return rackRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los racks: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Rack, asignando el usuario creador
     */
    public Rack crear(Rack rack, Short idUsuario) {
        try {
            rack.setNombreRacks(rack.getNombreRacks());
            rack.setPisoRacks(rack.getPisoRacks());
            rack.setRegistroActivo(true);
            rack.setCreadoPorUsuario(idUsuario);
            rack.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            rack.setModificadoPorUsuario(null);
            rack.setFechaModificacion(null);

            return rackRepository.save(rack);
        } catch (Exception e) {
            throw new InsertException("Error al insertar el rack: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Rack existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return rackRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del rack: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Rack (Desactiva el anterior, crea uno nuevo y actualiza referencias en Switch)
     */
    public Rack actualizar(Short id, Rack nuevosDatos, Short idUsuario) {
        try {
            Rack rackExistente = rackRepository.findById(id)
                    .filter(Rack::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Rack no encontrado con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            rackExistente.setRegistroActivo(false);
            rackExistente.setModificadoPorUsuario(idUsuario);
            rackExistente.setFechaModificacion(LocalDateTime.now());
            rackRepository.save(rackExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Rack nuevoRack = new Rack();
            nuevoRack.setNombreRacks(nuevosDatos.getNombreRacks());
            nuevoRack.setPisoRacks(nuevosDatos.getPisoRacks());
            nuevoRack.setRegistroActivo(true);
            nuevoRack.setCreadoPorUsuario(idUsuario);
            nuevoRack.setFechaCreacion(LocalDateTime.now());

            // Guardar el nuevo rack
            Rack rackGuardado = rackRepository.save(nuevoRack);

            // 3️⃣ **Actualizar todos los Switches que referenciaban el Rack anterior**
            List<Switch> switches = switchRepository.findByRack(rackExistente);
            for (Switch sw : switches) {
                sw.setRack(rackGuardado);
            }
            switchRepository.saveAll(switches);

            return rackGuardado;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el rack: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Rack (Cambia estado a inactivo y deja nulos los FK en Switch)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Rack rack = rackRepository.findById(id)
                    .filter(Rack::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Rack no encontrado con ID: " + id));

            // 1️⃣ **Actualizar todos los Switches que referenciaban este Rack a NULL**
            List<Switch> switches = switchRepository.findByRack(rack);
            for (Switch sw : switches) {
                sw.setRack(null);
            }
            switchRepository.saveAll(switches);

            // 2️⃣ Desactivar el Rack
            rack.setRegistroActivo(false);
            rack.setModificadoPorUsuario(idUsuario);
            rack.setFechaModificacion(LocalDateTime.now());
            rackRepository.save(rack);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el rack: " + e.getMessage());
        }
    }
}

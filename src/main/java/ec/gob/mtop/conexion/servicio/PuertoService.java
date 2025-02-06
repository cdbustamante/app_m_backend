package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.AccessPoint;
import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.VlanSwitch;
import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.repositorio.AccessPointRepository;
import ec.gob.mtop.conexion.repositorio.InterfazRepository;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import ec.gob.mtop.conexion.repositorio.VlanSwitchRepository;
import ec.gob.mtop.conexion.repositorio.SwitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PuertoService {

    @Autowired
    private PuertoRepository puertoRepository;

    @Autowired
    private VlanSwitchRepository vlanSwitchRepository;

    @Autowired
    private SwitchRepository switchRepository;
    
    @Autowired
    private InterfazRepository interfazRepository;

    @Autowired
    private AccessPointRepository accessPointRepository;

    /**
     * Buscar un Puerto por ID si está activo
     */
    public Puerto buscarPorId(Short id) {
        try {
            return puertoRepository.findById(id)
                    .filter(Puerto::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el puerto: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Puertos activos
     */
    public List<Puerto> listarTodos() {
        try {
            return puertoRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los puertos: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Puerto, asignando el usuario creador y validando la VLAN y el Switch
     */
    public Puerto crear(Puerto puerto, Short idUsuario, Short idVlanSwitch, Short idSwitchRed) {
        try {
            // Verificar si la VLAN existe
            VlanSwitch vlanSwitch = vlanSwitchRepository.findById(idVlanSwitch)
                    .orElseThrow(() -> new ResourceNotFoundException("VLAN no encontrada con ID: " + idVlanSwitch));

            // Verificar si el Switch existe
            Switch switchRed = switchRepository.findById(idSwitchRed)
                    .orElseThrow(() -> new ResourceNotFoundException("Switch no encontrado con ID: " + idSwitchRed));

            puerto.setNombrePuerto(puerto.getNombrePuerto());
            puerto.setEtiquetaPuerto(puerto.getEtiquetaPuerto());
            puerto.setEstadoPuerto(puerto.getEstadoPuerto());
            puerto.setVlanSwitch(vlanSwitch);
            puerto.setSwitchRed(switchRed);
            puerto.setRegistroActivo(true);
            puerto.setCreadoPorUsuario(idUsuario);
            puerto.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            puerto.setModificadoPorUsuario(null);
            puerto.setFechaModificacion(null);

            return puertoRepository.save(puerto);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar el puerto: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Puerto existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return puertoRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del puerto: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Puerto (Desactiva el anterior, crea uno nuevo y actualiza referencias en Interfaz y AccessPoint)
     */
    public Puerto actualizar(Short id, Puerto nuevosDatos, Short idUsuario, Short idVlanSwitch, Short idSwitchRed) {
        try {
            Puerto puertoExistente = puertoRepository.findById(id)
                    .filter(Puerto::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + id));

            // Verificar si la VLAN Switch y el Switch existen
            VlanSwitch vlanSwitch = vlanSwitchRepository.findById(idVlanSwitch)
                    .orElseThrow(() -> new ResourceNotFoundException("VLAN Switch no encontrada con ID: " + idVlanSwitch));

            Switch switchRed = switchRepository.findById(idSwitchRed)
                    .orElseThrow(() -> new ResourceNotFoundException("Switch no encontrado con ID: " + idSwitchRed));

            // 1️⃣ Desactivar el registro antiguo
            puertoExistente.setRegistroActivo(false);
            puertoExistente.setModificadoPorUsuario(idUsuario);
            puertoExistente.setFechaModificacion(LocalDateTime.now());
            puertoRepository.save(puertoExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Puerto nuevoPuerto = new Puerto();
            nuevoPuerto.setNombrePuerto(nuevosDatos.getNombrePuerto());
            nuevoPuerto.setEtiquetaPuerto(nuevosDatos.getEtiquetaPuerto());
            nuevoPuerto.setEstadoPuerto(nuevosDatos.getEstadoPuerto());
            nuevoPuerto.setVlanSwitch(vlanSwitch);
            nuevoPuerto.setSwitchRed(switchRed);
            nuevoPuerto.setRegistroActivo(true);
            nuevoPuerto.setCreadoPorUsuario(idUsuario);
            nuevoPuerto.setFechaCreacion(LocalDateTime.now());

            // Guardar el nuevo Puerto
            Puerto puertoGuardado = puertoRepository.save(nuevoPuerto);

            // 3️⃣ **Actualizar todas las Interfaces que referenciaban el Puerto anterior**
            List<Interfaz> interfaces = interfazRepository.findByPuerto(puertoExistente);
            for (Interfaz interfaz : interfaces) {
                interfaz.setPuerto(puertoGuardado);
            }
            interfazRepository.saveAll(interfaces);

            // 4️⃣ **Actualizar todos los AccessPoints que referenciaban el Puerto anterior**
            List<AccessPoint> accessPoints = accessPointRepository.findByPuerto(puertoExistente);
            for (AccessPoint accessPoint : accessPoints) {
                accessPoint.setPuerto(puertoGuardado);
            }
            accessPointRepository.saveAll(accessPoints);

            return puertoGuardado;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el Puerto: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Puerto (Cambia estado a inactivo y deja nulos los FK en Interfaz y AccessPoint)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Puerto puerto = puertoRepository.findById(id)
                    .filter(Puerto::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + id));

            // 1️⃣ **Actualizar todas las Interfaces que referenciaban este Puerto a NULL**
            List<Interfaz> interfaces = interfazRepository.findByPuerto(puerto);
            for (Interfaz interfaz : interfaces) {
                interfaz.setPuerto(null);
            }
            interfazRepository.saveAll(interfaces);

            // 2️⃣ **Actualizar todos los AccessPoints que referenciaban este Puerto a NULL**
            List<AccessPoint> accessPoints = accessPointRepository.findByPuerto(puerto);
            for (AccessPoint accessPoint : accessPoints) {
                accessPoint.setPuerto(null);
            }
            accessPointRepository.saveAll(accessPoints);

            // 3️⃣ Desactivar el Puerto
            puerto.setRegistroActivo(false);
            puerto.setModificadoPorUsuario(idUsuario);
            puerto.setFechaModificacion(LocalDateTime.now());
            puertoRepository.save(puerto);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el Puerto: " + e.getMessage());
        }
    }
}

package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.AccessPoint;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.repositorio.AccessPointRepository;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessPointService {

    @Autowired
    private AccessPointRepository accessPointRepository;

    @Autowired
    private PuertoRepository puertoRepository;

    /**
     * Buscar un Access Point por ID si está activo
     */
    public AccessPoint buscarPorId(Short id) {
        try {
            return accessPointRepository.findById(id)
                    .filter(AccessPoint::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Access Point no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el Access Point: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Access Points activos
     */
    public List<AccessPoint> listarTodos() {
        try {
            return accessPointRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los Access Points: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Access Point, asignando el usuario creador y validando el Puerto
     */
    public AccessPoint crear(AccessPoint accessPoint, Short idUsuario, Short idPuerto) {
        try {
            // Verificar si el Puerto existe
            Puerto puerto = puertoRepository.findById(idPuerto)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + idPuerto));

            accessPoint.setNombreAp(accessPoint.getNombreAp());
            accessPoint.setModeloAp(accessPoint.getModeloAp());
            accessPoint.setMacEthAp(accessPoint.getMacEthAp());
            accessPoint.setIpAp(accessPoint.getIpAp());
            accessPoint.setPuerto(puerto);
            accessPoint.setRegistroActivo(true);
            accessPoint.setCreadoPorUsuario(idUsuario);
            accessPoint.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            accessPoint.setModificadoPorUsuario(null);
            accessPoint.setFechaModificacion(null);

            return accessPointRepository.save(accessPoint);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar el Access Point: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Access Point existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return accessPointRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del Access Point: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Access Point (desactiva el anterior y crea uno nuevo)
     */
    public AccessPoint actualizar(Short id, AccessPoint nuevosDatos, Short idUsuario, Short idPuerto) {
        try {
            AccessPoint accessPointExistente = accessPointRepository.findById(id)
                    .filter(AccessPoint::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Access Point no encontrado con ID: " + id));

            // Verificar si el Puerto existe
            Puerto puerto = puertoRepository.findById(idPuerto)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + idPuerto));

            // 1️⃣ Desactivar el registro antiguo
            accessPointExistente.setRegistroActivo(false);
            accessPointExistente.setModificadoPorUsuario(idUsuario);
            accessPointExistente.setFechaModificacion(LocalDateTime.now());
            accessPointRepository.save(accessPointExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            AccessPoint nuevoAccessPoint = new AccessPoint();
            nuevoAccessPoint.setNombreAp(nuevosDatos.getNombreAp());
            nuevoAccessPoint.setModeloAp(nuevosDatos.getModeloAp());
            nuevoAccessPoint.setMacEthAp(nuevosDatos.getMacEthAp());
            nuevoAccessPoint.setIpAp(nuevosDatos.getIpAp());
            nuevoAccessPoint.setPuerto(puerto);
            nuevoAccessPoint.setRegistroActivo(true);
            nuevoAccessPoint.setCreadoPorUsuario(idUsuario);
            nuevoAccessPoint.setFechaCreacion(LocalDateTime.now());

            return accessPointRepository.save(nuevoAccessPoint);

        } catch (ResourceNotFoundException e) {
            throw e; // Se lanza directamente si no se encuentra el registro
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el Access Point: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Access Point (cambia estado a inactivo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            AccessPoint accessPoint = accessPointRepository.findById(id)
                    .filter(AccessPoint::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Access Point no encontrado con ID: " + id));

            accessPoint.setRegistroActivo(false);
            accessPoint.setModificadoPorUsuario(idUsuario);
            accessPoint.setFechaModificacion(LocalDateTime.now());
            accessPointRepository.save(accessPoint);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el Access Point: " + e.getMessage());
        }
    }
}

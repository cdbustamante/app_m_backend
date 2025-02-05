package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.repositorio.InterfazRepository;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import ec.gob.mtop.conexion.repositorio.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterfazService {

    @Autowired
    private InterfazRepository interfazRepository;

    @Autowired
    private PuertoRepository puertoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    /**
     * Buscar una Interfaz por ID si está activa
     */
    public Interfaz buscarPorId(Short id) {
        try {
            return interfazRepository.findById(id)
                    .filter(Interfaz::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Interfaz no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener la interfaz: " + e.getMessage());
        }
    }

    /**
     * Listar todas las Interfaces activas
     */
    public List<Interfaz> listarTodos() {
        try {
            return interfazRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar las interfaces: " + e.getMessage());
        }
    }

    /**
     * Crear una nueva Interfaz, asignando el usuario creador y validando el Puerto y el Equipo
     */
    public Interfaz crear(Interfaz interfaz, Short idUsuario, Short idPuerto, Short idEquipo) {
        try {
            // Verificar si el Puerto existe
            Puerto puerto = puertoRepository.findById(idPuerto)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + idPuerto));

            // Verificar si el Equipo existe
            Equipo equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipo));

            interfaz.setMacIface(interfaz.getMacIface());
            interfaz.setIpIface(interfaz.getIpIface());
            interfaz.setPuerto(puerto);
            interfaz.setEquipo(equipo);
            interfaz.setRegistroActivo(true);
            interfaz.setCreadoPorUsuario(idUsuario);
            interfaz.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            interfaz.setModificadoPorUsuario(null);
            interfaz.setFechaModificacion(null);

            return interfazRepository.save(interfaz);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar la interfaz: " + e.getMessage());
        }
    }

    /**
     * Verificar si una Interfaz existe y está activa
     */
    public boolean existePorId(Short id) {
        try {
            return interfazRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia de la interfaz: " + e.getMessage());
        }
    }

    /**
     * Actualizar una Interfaz (desactiva la anterior y crea una nueva)
     */
    public Interfaz actualizar(Short id, Interfaz nuevosDatos, Short idUsuario, Short idPuerto, Short idEquipo) {
        try {
            Interfaz interfazExistente = interfazRepository.findById(id)
                    .filter(Interfaz::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Interfaz no encontrada con ID: " + id));

            // Verificar si el Puerto existe
            Puerto puerto = puertoRepository.findById(idPuerto)
                    .orElseThrow(() -> new ResourceNotFoundException("Puerto no encontrado con ID: " + idPuerto));

            // Verificar si el Equipo existe
            Equipo equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipo));

            // 1️⃣ Desactivar el registro antiguo
            interfazExistente.setRegistroActivo(false);
            interfazExistente.setModificadoPorUsuario(idUsuario);
            interfazExistente.setFechaModificacion(LocalDateTime.now());
            interfazRepository.save(interfazExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Interfaz nuevaInterfaz = new Interfaz();
            nuevaInterfaz.setMacIface(nuevosDatos.getMacIface());
            nuevaInterfaz.setIpIface(nuevosDatos.getIpIface());
            nuevaInterfaz.setPuerto(puerto);
            nuevaInterfaz.setEquipo(equipo);
            nuevaInterfaz.setRegistroActivo(true);
            nuevaInterfaz.setCreadoPorUsuario(idUsuario);
            nuevaInterfaz.setFechaCreacion(LocalDateTime.now());

            return interfazRepository.save(nuevaInterfaz);

        } catch (ResourceNotFoundException e) {
            throw e; // Se lanza directamente si no se encuentra el registro
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar la interfaz: " + e.getMessage());
        }
    }

    /**
     * Eliminar una Interfaz (cambia estado a inactivo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Interfaz interfaz = interfazRepository.findById(id)
                    .filter(Interfaz::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Interfaz no encontrada con ID: " + id));

            interfaz.setRegistroActivo(false);
            interfaz.setModificadoPorUsuario(idUsuario);
            interfaz.setFechaModificacion(LocalDateTime.now());
            interfazRepository.save(interfaz);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar la interfaz: " + e.getMessage());
        }
    }
}

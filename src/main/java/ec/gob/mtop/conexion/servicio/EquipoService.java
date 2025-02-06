package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.modelo.TipoEquipo;
import ec.gob.mtop.conexion.repositorio.EquipoRepository;
import ec.gob.mtop.conexion.repositorio.PersonaRepository;
import ec.gob.mtop.conexion.repositorio.TipoEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;

    /**
     * Buscar un Equipo por ID si está activo
     */
    public Equipo buscarPorId(Short id) {
        try {
            return equipoRepository.findById(id)
                    .filter(Equipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el equipo: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Equipos activos
     */
    public List<Equipo> listarTodos() {
        try {
            return equipoRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los equipos: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Equipo, asignando el usuario creador y validando el Tipo de Equipo y el Usuario Responsable
     */
    public Equipo crear(Equipo equipo, Short idUsuario, Short idTipoEquipo, Short idUsuarioEquipo) {
        try {
            // Verificar si el Tipo de Equipo existe
            TipoEquipo tipoEquipo = tipoEquipoRepository.findById(idTipoEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de equipo no encontrado con ID: " + idTipoEquipo));

            // Verificar si el Usuario Responsable existe
            Persona usuarioEquipo = personaRepository.findById(idUsuarioEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuarioEquipo));

            equipo.setNombreEquipo(equipo.getNombreEquipo());
            equipo.setTipoEquipo(tipoEquipo);
            equipo.setUsuarioEquipo(usuarioEquipo);
            equipo.setRegistroActivo(true);
            equipo.setCreadoPorUsuario(idUsuario);
            equipo.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            equipo.setModificadoPorUsuario(null);
            equipo.setFechaModificacion(null);

            return equipoRepository.save(equipo);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar el equipo: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Equipo existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return equipoRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del equipo: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Equipo (desactiva el anterior y crea uno nuevo)
     */
    public Equipo actualizar(Short id, Equipo nuevosDatos, Short idUsuario, Short idTipoEquipo, Short idUsuarioEquipo) {
        try {
            Equipo equipoExistente = equipoRepository.findById(id)
                    .filter(Equipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));

            // Verificar si el Tipo de Equipo existe
            TipoEquipo tipoEquipo = tipoEquipoRepository.findById(idTipoEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de equipo no encontrado con ID: " + idTipoEquipo));

            // Verificar si el Usuario Responsable existe
            Persona usuarioEquipo = personaRepository.findById(idUsuarioEquipo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuarioEquipo));

            // 1️⃣ Desactivar el registro antiguo
            equipoExistente.setRegistroActivo(false);
            equipoExistente.setModificadoPorUsuario(idUsuario);
            equipoExistente.setFechaModificacion(LocalDateTime.now());
            equipoRepository.save(equipoExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Equipo nuevoEquipo = new Equipo();
            nuevoEquipo.setNombreEquipo(nuevosDatos.getNombreEquipo());
            nuevoEquipo.setTipoEquipo(tipoEquipo);
            nuevoEquipo.setUsuarioEquipo(usuarioEquipo);
            nuevoEquipo.setRegistroActivo(true);
            nuevoEquipo.setCreadoPorUsuario(idUsuario);
            nuevoEquipo.setFechaCreacion(LocalDateTime.now());

            return equipoRepository.save(nuevoEquipo);

        } catch (ResourceNotFoundException e) {
            throw e; // Se lanza directamente si no se encuentra el registro
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el equipo: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Equipo (cambia estado a inactivo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Equipo equipo = equipoRepository.findById(id)
                    .filter(Equipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));

            equipo.setRegistroActivo(false);
            equipo.setModificadoPorUsuario(idUsuario);
            equipo.setFechaModificacion(LocalDateTime.now());
            equipoRepository.save(equipo);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el equipo: " + e.getMessage());
        }
    }
}

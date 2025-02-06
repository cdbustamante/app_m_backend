package ec.gob.mtop.conexion.servicio;


import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.modelo.TipoEquipo;
import ec.gob.mtop.conexion.repositorio.EquipoRepository;
import ec.gob.mtop.conexion.repositorio.TipoEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TipoEquipoService {

    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;

    /**
     * Buscar un Tipo de Equipo por ID si está activo
     */
    public TipoEquipo buscarPorId(Short id) {
        try {
            return tipoEquipoRepository.findById(id)
                    .filter(TipoEquipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de equipo no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el tipo de equipo: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Tipos de Equipo activos
     */
    public List<TipoEquipo> listarTodos() {
        try {
            return tipoEquipoRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los tipos de equipo: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Tipo de Equipo, asignando el usuario creador
     */
    public TipoEquipo crear(TipoEquipo tipoEquipo, Short idUsuario) {
        try {
            tipoEquipo.setNombreTipo(tipoEquipo.getNombreTipo());
            tipoEquipo.setRegistroActivo(true);
            tipoEquipo.setCreadoPorUsuario(idUsuario);
            tipoEquipo.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            tipoEquipo.setModificadoPorUsuario(null);
            tipoEquipo.setFechaModificacion(null);

            return tipoEquipoRepository.save(tipoEquipo);
        } catch (Exception e) {
            throw new InsertException("Error al insertar el tipo de equipo: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Tipo de Equipo existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return tipoEquipoRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del tipo de equipo: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Tipo de Equipo (Desactiva el anterior, crea uno nuevo y actualiza referencias en Equipo)
     */
    public TipoEquipo actualizar(Short id, TipoEquipo nuevosDatos, Short idUsuario) {
        try {
            TipoEquipo tipoEquipoExistente = tipoEquipoRepository.findById(id)
                    .filter(TipoEquipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de equipo no encontrado con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            tipoEquipoExistente.setRegistroActivo(false);
            tipoEquipoExistente.setModificadoPorUsuario(idUsuario);
            tipoEquipoExistente.setFechaModificacion(LocalDateTime.now());
            tipoEquipoRepository.save(tipoEquipoExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            TipoEquipo nuevoTipoEquipo = new TipoEquipo();
            nuevoTipoEquipo.setNombreTipo(nuevosDatos.getNombreTipo());
            nuevoTipoEquipo.setRegistroActivo(true);
            nuevoTipoEquipo.setCreadoPorUsuario(idUsuario);
            nuevoTipoEquipo.setFechaCreacion(LocalDateTime.now());

            // Guardar el nuevo tipo de equipo
            TipoEquipo tipoEquipoGuardado = tipoEquipoRepository.save(nuevoTipoEquipo);

            // 3️⃣ **Actualizar todos los Equipos que referenciaban el Tipo de Equipo anterior**
            List<Equipo> equipos = equipoRepository.findByTipoEquipo(tipoEquipoExistente);
            for (Equipo equipo : equipos) {
                equipo.setTipoEquipo(tipoEquipoGuardado);
            }
            equipoRepository.saveAll(equipos);

            return tipoEquipoGuardado;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el tipo de equipo: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Tipo de Equipo (Cambia estado a inactivo y deja nulos los FK en Equipo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            TipoEquipo tipoEquipo = tipoEquipoRepository.findById(id)
                    .filter(TipoEquipo::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de equipo no encontrado con ID: " + id));

            // 1️⃣ **Actualizar todos los Equipos que referenciaban este Tipo de Equipo a NULL**
            List<Equipo> equipos = equipoRepository.findByTipoEquipo(tipoEquipo);
            for (Equipo equipo : equipos) {
                
                equipo.setRegistroActivo(false);
                equipo.setModificadoPorUsuario(idUsuario);
                equipo.setFechaModificacion(LocalDateTime.now());
                equipoRepository.save(equipo);

            }
            equipoRepository.saveAll(equipos);

            // 2️⃣ Desactivar el Tipo de Equipo
            tipoEquipo.setRegistroActivo(false);
            tipoEquipo.setModificadoPorUsuario(idUsuario);
            tipoEquipo.setFechaModificacion(LocalDateTime.now());
            tipoEquipoRepository.save(tipoEquipo);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el tipo de equipo: " + e.getMessage());
        }
    }
}


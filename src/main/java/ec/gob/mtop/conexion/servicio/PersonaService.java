package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.repositorio.DepartamentoRepository;
import ec.gob.mtop.conexion.repositorio.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    /**
     * Buscar una Persona por ID si está activa
     */
    public Persona buscarPorId(Short id) {
        try {
            return personaRepository.findById(id)
                    .filter(Persona::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener la persona: " + e.getMessage());
        }
    }

    /**
     * Listar todas las Personas activas
     */
    public List<Persona> listarTodos() {
        try {
            return personaRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar las personas: " + e.getMessage());
        }
    }

    /**
     * Crear una nueva Persona, asignando el usuario creador y validando el Departamento
     */
    public Persona crear(Persona persona, Short idUsuario, Integer idDepartamento) {
        try {
            // Verificar si el Departamento existe
            Departamento departamento = departamentoRepository.findById(idDepartamento)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + idDepartamento));

            persona.setCedula(persona.getCedula());
            persona.setNombres(persona.getNombres());
            persona.setAlias(persona.getAlias());
            persona.setDepartamento(departamento);
            persona.setRegistroActivo(true);
            persona.setCreadoPorUsuario(idUsuario);
            persona.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            persona.setModificadoPorUsuario(null);
            persona.setFechaModificacion(null);

            return personaRepository.save(persona);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error al insertar la persona: " + e.getMessage());
        }
    }

    /**
     * Verificar si una Persona existe y está activa
     */
    public boolean existePorId(Short id) {
        try {
            return personaRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia de la persona: " + e.getMessage());
        }
    }

    /**
     * Actualizar una Persona (desactiva el anterior y crea uno nuevo)
     */
    public Persona actualizar(Short id, Persona nuevosDatos, Short idUsuario, Integer idDepartamento) {
        try {
            Persona personaExistente = personaRepository.findById(id)
                    .filter(Persona::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));

            // Verificar si el Departamento existe
            Departamento departamento = departamentoRepository.findById(idDepartamento)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + idDepartamento));

            // 1️⃣ Desactivar el registro antiguo
            personaExistente.setRegistroActivo(false);
            personaExistente.setModificadoPorUsuario(idUsuario);
            personaExistente.setFechaModificacion(LocalDateTime.now());
            personaRepository.save(personaExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Persona nuevaPersona = new Persona();
            nuevaPersona.setCedula(nuevosDatos.getCedula());
            nuevaPersona.setNombres(nuevosDatos.getNombres());
            nuevaPersona.setAlias(nuevosDatos.getAlias());
            nuevaPersona.setDepartamento(departamento);
            nuevaPersona.setRegistroActivo(true);
            nuevaPersona.setCreadoPorUsuario(idUsuario);
            nuevaPersona.setFechaCreacion(LocalDateTime.now());

            return personaRepository.save(nuevaPersona);

        } catch (ResourceNotFoundException e) {
            throw e; // Se lanza directamente si no se encuentra el registro
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar la persona: " + e.getMessage());
        }
    }

    /**
     * Eliminar una Persona (cambia estado a inactivo)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Persona persona = personaRepository.findById(id)
                    .filter(Persona::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));

            persona.setRegistroActivo(false);
            persona.setModificadoPorUsuario(idUsuario);
            persona.setFechaModificacion(LocalDateTime.now());
            personaRepository.save(persona);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar la persona: " + e.getMessage());
        }
    }
}

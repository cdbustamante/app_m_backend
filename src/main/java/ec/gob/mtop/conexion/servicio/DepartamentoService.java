package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.repositorio.DepartamentoRepository;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.repositorio.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Autowired
    private PersonaRepository personaRepository;

    /**
     * Buscar un Departamento por ID si está activo
     */
    public Departamento buscarPorId(Short id) {
        try {
            return departamentoRepository.findById(id)
                    .filter(Departamento::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el departamento: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Departamentos activos
     */
    public List<Departamento> listarTodos() {
        try {
            return departamentoRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los departamentos: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Departamento, asignando el usuario creador
     */
    public Departamento crear(Departamento departamento, Short idUsuario) {
        try {
            // 1️⃣ Establecer los atributos correctos antes de guardar
            departamento.setNombreDepar(departamento.getNombreDepar());
            departamento.setCodigoDepar(departamento.getCodigoDepar());
            departamento.setRegistroActivo(true);
            departamento.setCreadoPorUsuario(idUsuario);
            departamento.setFechaCreacion(LocalDateTime.now());

            // 2️⃣ Los campos de modificación deben ser nulos en la creación
            departamento.setModificadoPorUsuario(null);
            departamento.setFechaModificacion(null);

            return departamentoRepository.save(departamento);
        } catch (Exception e) {
            throw new InsertException("Error al insertar el departamento: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Departamento existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return departamentoRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del departamento: " + e.getMessage());
        }
    }

    /**
     * Actualizar un Departamento (Desactiva el anterior, crea uno nuevo y actualiza referencias en Persona)
     */
    public Departamento actualizar(Short id, Departamento nuevosDatos, Short idUsuario) {
        try {
            Departamento departamentoExistente = departamentoRepository.findById(id)
                    .filter(Departamento::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + id));

            // 1️⃣ Desactivar el registro antiguo
            departamentoExistente.setRegistroActivo(false);
            departamentoExistente.setModificadoPorUsuario(idUsuario);
            departamentoExistente.setFechaModificacion(LocalDateTime.now());
            departamentoRepository.save(departamentoExistente);

            // 2️⃣ Crear un nuevo registro con datos actualizados
            Departamento nuevoDepartamento = new Departamento();
            nuevoDepartamento.setNombreDepar(nuevosDatos.getNombreDepar());
            nuevoDepartamento.setCodigoDepar(nuevosDatos.getCodigoDepar());
            nuevoDepartamento.setRegistroActivo(true);
            nuevoDepartamento.setCreadoPorUsuario(idUsuario);
            nuevoDepartamento.setFechaCreacion(LocalDateTime.now());

            // Guardar el nuevo departamento
            Departamento departamentoGuardado = departamentoRepository.save(nuevoDepartamento);

            // 3️⃣ **Actualizar todas las Personas que referenciaban el departamento anterior**
            List<Persona> personas = personaRepository.findByDepartamento(departamentoExistente);
            for (Persona persona : personas) {
                persona.setDepartamento(departamentoGuardado);
            }
            personaRepository.saveAll(personas);

            return departamentoGuardado;

        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el departamento: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Departamento (Cambia estado a inactivo y deja nulos los FK en Persona)
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Departamento departamento = departamentoRepository.findById(id)
                    .filter(Departamento::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + id));

            // 1️⃣ **Actualizar todas las Personas que referenciaban este departamento a NULL**
            List<Persona> personas = personaRepository.findByDepartamento(departamento);
            for (Persona persona : personas) {
                persona.setDepartamento(null);
            }
            personaRepository.saveAll(personas);

            // 2️⃣ Desactivar el departamento
            departamento.setRegistroActivo(false);
            departamento.setModificadoPorUsuario(idUsuario);
            departamento.setFechaModificacion(LocalDateTime.now());
            departamentoRepository.save(departamento);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el departamento: " + e.getMessage());
        }
    }
}

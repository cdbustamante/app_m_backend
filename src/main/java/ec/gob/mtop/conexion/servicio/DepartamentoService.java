package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.repositorio.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

   // 🔹 Obtener departamentos con filtros y paginación
    public Page<Departamento> obtenerDepartamentosFiltrados(Specification<Departamento> specs, Pageable pageable) {
        return departamentoRepository.findAll(specs, pageable);
    }

    // Obtener un departamento por ID (solo si está activo)
    public Optional<Departamento> obtenerPorId(Integer id) {
        return departamentoRepository.findByIdAndRegistroActivoTrue(id);
    }

    // Crear un departamento (solo admin)
    public Departamento crearDepartamento(Departamento departamento, Short creadoPorUsuario) {
        departamento.setCreadoPorUsuario(creadoPorUsuario);
        departamento.setFechaCreacion(LocalDateTime.now());
        departamento.setRegistroActivo(true);
        return departamentoRepository.save(departamento);
    }
    

    // Actualizar un departamento (solo admin)
    public Departamento actualizarDepartamento(Integer id, Departamento detalles, Short modificadoPor) {
        return departamentoRepository.findByIdAndRegistroActivoTrue(id)
                .map(departamento -> {
                    departamento.setNombreDepar(detalles.getNombreDepar());
                    departamento.setCodigoDepar(detalles.getCodigoDepar());
                    departamento.setModificadoPorUsuario(modificadoPor);
                    departamento.setFechaModificacion(LocalDateTime.now());
                    return departamentoRepository.save(departamento);
                }).orElseThrow(() -> new RuntimeException("Departamento no encontrado o inactivo con ID: " + id));
    }

    // Borrado lógico del departamento (solo admin)
    public void eliminarDepartamento(Integer id, Short modificadoPor) {
        departamentoRepository.findById(id).ifPresent(departamento -> {
            departamento.setRegistroActivo(false);
            departamento.setModificadoPorUsuario(modificadoPor);
            departamento.setFechaModificacion(LocalDateTime.now());
            departamentoRepository.save(departamento);
            
        });
    }
        // 🔹 Crear especificación de filtros
    public Specification<Departamento> crearEspecificacionFiltro(
            String codigo, String nombre, String fechaCreacion, String fechaModificacion, String creador, String modificador) {

        return (root, query, criteriaBuilder) -> {
            Specification<Departamento> spec = Specification.where(null);

            if (codigo != null && !codigo.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(root1.get("codigoDepar"), "%" + codigo + "%"));
            }
            if (nombre != null && !nombre.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(root1.get("nombreDepar"), "%" + nombre + "%"));
            }
            if (fechaCreacion != null && !fechaCreacion.isEmpty()) {
                LocalDate fecha = LocalDate.parse(fechaCreacion, DateTimeFormatter.ISO_DATE);
                spec = spec.and((root1, query1, cb) -> 
                    cb.equal(cb.function("DATE", LocalDate.class, root1.get("fechaCreacion")), fecha));
            }
            if (fechaModificacion != null && !fechaModificacion.isEmpty()) {
                LocalDate fecha = LocalDate.parse(fechaModificacion, DateTimeFormatter.ISO_DATE);
                spec = spec.and((root1, query1, cb) -> 
                    cb.equal(cb.function("DATE", LocalDate.class, root1.get("fechaModificacion")), fecha));
            }
            if (creador != null && !creador.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("creadoPorUsuario"), Short.parseShort(creador)));
            }
            if (modificador != null && !modificador.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("modificadoPorUsuario"), Short.parseShort(modificador)));
            }
            // 🔹 Filtrar solo los departamentos activos
            spec = spec.and((root1, query1, cb) -> cb.isTrue(root1.get("registroActivo")));

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}

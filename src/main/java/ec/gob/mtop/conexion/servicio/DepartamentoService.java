package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.repositorio.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    // Obtener todos los departamentos activos
    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findByRegistroActivoTrue();
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
}

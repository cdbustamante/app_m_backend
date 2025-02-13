package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Departamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Integer>, JpaSpecificationExecutor<Departamento> {

    // 🔹 Obtener departamentos activos con paginación
    Page<Departamento> findAllByRegistroActivoTrue(Pageable pageable);

    // 🔹 Obtener un departamento activo por ID
    Optional<Departamento> findByIdAndRegistroActivoTrue(Integer id);

    // 🔹 Obtener todos los departamentos con filtros dinámicos
    Page<Departamento> findAll(Specification<Departamento> spec, Pageable pageable);
}

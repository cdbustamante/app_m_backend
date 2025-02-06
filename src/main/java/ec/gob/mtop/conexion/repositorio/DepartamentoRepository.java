package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Departamento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Short> {
	 // Retorna solo los departamentos activos
    List<Departamento> findByRegistroActivoTrue();

    // Verifica si existe un departamento activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
}


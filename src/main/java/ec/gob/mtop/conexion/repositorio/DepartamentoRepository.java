package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
    
    // Obtener todos los departamentos activos
    List<Departamento> findByRegistroActivoTrue();

    // Buscar un departamento activo por ID
    Optional<Departamento> findByIdAndRegistroActivoTrue(Integer id);
}

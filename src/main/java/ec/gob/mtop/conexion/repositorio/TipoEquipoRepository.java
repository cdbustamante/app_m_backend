package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.TipoEquipo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEquipoRepository extends JpaRepository<TipoEquipo, Short> {
	// Retorna solo los tipos de equipo activos
    List<TipoEquipo> findByRegistroActivoTrue();

    // Verifica si existe un tipo de equipo activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
}

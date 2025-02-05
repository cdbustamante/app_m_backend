package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.modelo.Persona;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Short> {
	// Retorna solo las personas activas
    List<Persona> findByRegistroActivoTrue();

    // Verifica si existe una persona activa con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Persona> findByDepartamento(Departamento departamento);
}

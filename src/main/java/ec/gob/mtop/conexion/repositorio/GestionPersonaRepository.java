package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GestionPersonaRepository extends JpaRepository<Persona, Integer> {
    // Buscar persona por cédula y verificar si está activa
    Optional<Persona> findByCedulaAndRegistroActivoTrue(String cedula);
}

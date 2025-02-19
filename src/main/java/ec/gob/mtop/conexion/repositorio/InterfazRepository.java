package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.Equipo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfazRepository extends JpaRepository<Interfaz, Short> {
	 // Retorna solo las Interfaces activas
    List<Interfaz> findByRegistroActivoTrue();

    // Verifica si existe una Interfaz activa con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Interfaz> findByPuerto(Puerto puerto);
    List<Interfaz> findByEquipoAndRegistroActivoTrue(Equipo equipo);


}


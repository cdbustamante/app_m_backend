package ec.gob.mtop.conexion.repositorio;

import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.modelo.TipoEquipo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Short> {
	 // Retorna solo los equipos activos
    List<Equipo> findByRegistroActivoTrue();

    // Verifica si existe un equipo activo con el ID dado
    boolean existsByIdAndRegistroActivoTrue(Short id);
    
    List<Equipo> findByTipoEquipo(TipoEquipo tipoEquipo);
    
    List<Equipo> findByUsuarioEquipo(Persona usuarioEquipo);


}

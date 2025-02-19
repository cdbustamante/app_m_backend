package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.dto.PersonaConEquiposDTO;
import ec.gob.mtop.conexion.dto.EquipoInfoDTO;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.modelo.TipoEquipo;
import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.repositorio.PersonaRepository;
import ec.gob.mtop.conexion.repositorio.EquipoRepository;
import ec.gob.mtop.conexion.repositorio.InterfazRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GestionPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private InterfazRepository interfazRepository;

    public PersonaConEquiposDTO obtenerEquiposDePersona(String cedula) {
        // Buscar persona por cédula
        Persona persona = personaRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Obtener los equipos asignados a esta persona
        List<Equipo> equipos = equipoRepository.findByUsuarioEquipo(persona);

        // Agrupar los equipos por conexión (Alámbrico/Inalámbrico) y por Tipo de Equipo
        Map<String, Map<String, List<EquipoInfoDTO>>> equiposPorConexion = new HashMap<>();

        for (Equipo equipo : equipos) {
            // Obtener el Tipo de Equipo
            TipoEquipo tipoEquipo = equipo.getTipoEquipo();
            String tipoEquipoNombre = (tipoEquipo != null) ? tipoEquipo.getNombreTipo() : "Desconocido";

            // Obtener la interfaz del equipo para determinar si es alámbrico o inalámbrico
            List<Interfaz> interfaces = interfazRepository.findByEquipoAndRegistroActivoTrue(equipo);

            for (Interfaz interfaz : interfaces) {
                String tipoConexion = interfaz.getTipoIface().equalsIgnoreCase("INALAMBRICO") ? "Inalámbricos" : "Alámbricos";

                // Inicializar estructuras si no existen
                equiposPorConexion.putIfAbsent(tipoConexion, new HashMap<>());
                equiposPorConexion.get(tipoConexion).putIfAbsent(tipoEquipoNombre, new ArrayList<>());

                // Crear el objeto DTO con IP y MAC
                EquipoInfoDTO equipoInfo = new EquipoInfoDTO(
                        equipo.getNombreEquipo(),
                        interfaz.getIpIface(),
                        interfaz.getMacIface()
                );

                // Agregar a la estructura final
                equiposPorConexion.get(tipoConexion).get(tipoEquipoNombre).add(equipoInfo);
            }
        }

        // Retornar el DTO con los datos de la persona y la lista de equipos organizados
        return new PersonaConEquiposDTO(
                persona.getCedula(),
                persona.getNombres(),
                persona.getAlias(),
                equiposPorConexion
        );
    }
    public PersonaConEquiposDTO actualizarPersona(String cedula, PersonaConEquiposDTO personaDTO) {
        // Buscar persona por cédula
        Persona persona = personaRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    
        // Actualizar Nombres y Alias
        persona.setNombres(personaDTO.getNombres());
        persona.setAlias(personaDTO.getAlias());
    
        // Guardar cambios en la BD
        personaRepository.save(persona);
    
        // Devolver la nueva información actualizada
        return new PersonaConEquiposDTO(
                persona.getCedula(),
                persona.getNombres(),
                persona.getAlias(),
                personaDTO.getEquiposPorConexion() // Retornamos los mismos equipos sin modificarlos
        );
    }
    
}

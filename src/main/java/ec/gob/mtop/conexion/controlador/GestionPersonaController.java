package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.dto.PersonaConEquiposDTO;
import ec.gob.mtop.conexion.servicio.GestionPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gestion-persona")
public class GestionPersonaController {

    @Autowired
    private GestionPersonaService gestionPersonaService;

    @GetMapping("/equipos/{cedula}")
    public PersonaConEquiposDTO obtenerEquiposDePersona(@PathVariable String cedula) {
        return gestionPersonaService.obtenerEquiposDePersona(cedula);
    }

    // 🔹 El método PUT debe estar dentro de la clase
    @PutMapping("/actualizar/{cedula}")
    public ResponseEntity<PersonaConEquiposDTO> actualizarPersona(
            @PathVariable String cedula,
            @RequestBody PersonaConEquiposDTO personaDTO) {
        
        PersonaConEquiposDTO personaActualizada = gestionPersonaService.actualizarPersona(cedula, personaDTO);
        return ResponseEntity.ok(personaActualizada);
    }
}

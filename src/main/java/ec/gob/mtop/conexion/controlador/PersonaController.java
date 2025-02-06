package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.servicio.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    /**
     * 📌 **Obtener una Persona por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(personaService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todas las Personas activas**
     */
    @GetMapping
    public ResponseEntity<List<Persona>> listarTodos() {
        return ResponseEntity.ok(personaService.listarTodos());
    }

    /**
     * 📌 **Crear una nueva Persona**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la crea.
     * 🔹 **Parámetro:** `idDepartamento` → ID del departamento al que pertenece la persona.
     * 🔹 **Body:** Objeto `Persona`
     */
    @PostMapping
    public ResponseEntity<Persona> crear(@RequestBody Persona persona, 
                                         @RequestParam Short idUsuario,
                                         @RequestParam(required = false) Short idDepartamento) {
        return ResponseEntity.ok(personaService.crear(persona, idUsuario, idDepartamento));
    }

    /**
     * 📌 **Actualizar una Persona**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la modifica.
     * 🔹 **Parámetro:** `idDepartamento` → ID del departamento al que pertenece la persona.
     * 🔹 **Body:** Objeto `Persona`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizar(@PathVariable Short id, 
                                              @RequestBody Persona persona, 
                                              @RequestParam Short idUsuario,
                                              @RequestParam(required = false) Short idDepartamento) {
        return ResponseEntity.ok(personaService.actualizar(id, persona, idUsuario, idDepartamento));
    }

    /**
     * 📌 **Eliminar una Persona (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        personaService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

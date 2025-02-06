package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.servicio.InterfazService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interfaces")
public class InterfazController {

    @Autowired
    private InterfazService interfazService;

    /**
     * 📌 **Obtener una Interfaz por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Interfaz> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(interfazService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todas las Interfaces activas**
     */
    @GetMapping
    public ResponseEntity<List<Interfaz>> listarTodos() {
        return ResponseEntity.ok(interfazService.listarTodos());
    }

    /**
     * 📌 **Crear una nueva Interfaz**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la crea.
     * 🔹 **Parámetro:** `idPuerto` → ID del puerto al que se conecta la interfaz (opcional).
     * 🔹 **Parámetro:** `idEquipo` → ID del equipo al que pertenece la interfaz (opcional).
     * 🔹 **Body:** Objeto `Interfaz`
     */
    @PostMapping
    public ResponseEntity<Interfaz> crear(@RequestBody Interfaz interfaz, 
                                          @RequestParam Short idUsuario,
                                          @RequestParam(required = false) Short idPuerto,
                                          @RequestParam(required = false) Short idEquipo) {
        return ResponseEntity.ok(interfazService.crear(interfaz, idUsuario, idPuerto, idEquipo));
    }

    /**
     * 📌 **Actualizar una Interfaz**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la modifica.
     * 🔹 **Parámetro:** `idPuerto` → ID del puerto al que se conecta la interfaz (opcional).
     * 🔹 **Parámetro:** `idEquipo` → ID del equipo al que pertenece la interfaz (opcional).
     * 🔹 **Body:** Objeto `Interfaz`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Interfaz> actualizar(@PathVariable Short id, 
                                               @RequestBody Interfaz interfaz, 
                                               @RequestParam Short idUsuario,
                                               @RequestParam(required = false) Short idPuerto,
                                               @RequestParam(required = false) Short idEquipo) {
        return ResponseEntity.ok(interfazService.actualizar(id, interfaz, idUsuario, idPuerto, idEquipo));
    }

    /**
     * 📌 **Eliminar una Interfaz (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        interfazService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

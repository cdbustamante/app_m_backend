package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.servicio.SwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/switches")
public class SwitchController {

    @Autowired
    private SwitchService switchService;

    /**
     * 📌 **Obtener un Switch por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Switch> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(switchService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Switches activos**
     */
    @GetMapping
    public ResponseEntity<List<Switch>> listarTodos() {
        return ResponseEntity.ok(switchService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Switch**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Parámetro:** `idRack` → ID del rack al que pertenece el switch.
     * 🔹 **Body:** Objeto `Switch`
     */
    @PostMapping
    public ResponseEntity<Switch> crear(@RequestBody Switch switchRed, 
                                        @RequestParam Short idUsuario,
                                        @RequestParam(required = false) Short idRack) {
        return ResponseEntity.ok(switchService.crear(switchRed, idUsuario, idRack));
    }

    /**
     * 📌 **Actualizar un Switch**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Body:** Objeto `Switch`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Switch> actualizar(@PathVariable Short id, 
                                             @RequestBody Switch switchRed, 
                                             @RequestParam Short idUsuario) {
        return ResponseEntity.ok(switchService.actualizar(id, switchRed, idUsuario));
    }

    /**
     * 📌 **Eliminar un Switch (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        switchService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

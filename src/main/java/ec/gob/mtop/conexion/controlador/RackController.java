package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Rack;
import ec.gob.mtop.conexion.servicio.RackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
public class RackController {

    @Autowired
    private RackService rackService;

    /**
     * 📌 **Obtener un Rack por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rack> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(rackService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Racks activos**
     */
    @GetMapping
    public ResponseEntity<List<Rack>> listarTodos() {
        return ResponseEntity.ok(rackService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Rack**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Body:** Objeto `Rack`
     */
    @PostMapping
    public ResponseEntity<Rack> crear(@RequestBody Rack rack, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(rackService.crear(rack, idUsuario));
    }

    /**
     * 📌 **Actualizar un Rack**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Body:** Objeto `Rack`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Rack> actualizar(@PathVariable Short id, @RequestBody Rack rack, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(rackService.actualizar(id, rack, idUsuario));
    }

    /**
     * 📌 **Eliminar un Rack (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        rackService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

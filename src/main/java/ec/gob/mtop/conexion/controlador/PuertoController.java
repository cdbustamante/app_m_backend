package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.servicio.PuertoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puertos")
public class PuertoController {

    @Autowired
    private PuertoService puertoService;

    /**
     * 📌 **Obtener un Puerto por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Puerto> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(puertoService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Puertos activos**
     */
    @GetMapping
    public ResponseEntity<List<Puerto>> listarTodos() {
        return ResponseEntity.ok(puertoService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Puerto**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Parámetro:** `idVlanSwitch` → ID de la VLAN asociada al puerto (opcional).
     * 🔹 **Parámetro:** `idSwitch` → ID del switch al que pertenece el puerto.
     * 🔹 **Body:** Objeto `Puerto`
     */
    @PostMapping
    public ResponseEntity<Puerto> crear(@RequestBody Puerto puerto, 
                                        @RequestParam Short idUsuario,
                                        @RequestParam(required = false) Short idVlanSwitch,
                                        @RequestParam Short idSwitch) {
        return ResponseEntity.ok(puertoService.crear(puerto, idUsuario, idVlanSwitch, idSwitch));
    }

    /**
     * 📌 **Actualizar un Puerto**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Parámetro:** `idVlanSwitch` → ID de la VLAN asociada al puerto (opcional).
     * 🔹 **Parámetro:** `idSwitch` → ID del switch al que pertenece el puerto.
     * 🔹 **Body:** Objeto `Puerto`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Puerto> actualizar(@PathVariable Short id, 
                                             @RequestBody Puerto puerto, 
                                             @RequestParam Short idUsuario,
                                             @RequestParam(required = false) Short idVlanSwitch,
                                             @RequestParam Short idSwitch) {
        return ResponseEntity.ok(puertoService.actualizar(id, puerto, idUsuario, idVlanSwitch, idSwitch));
    }

    /**
     * 📌 **Eliminar un Puerto (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        puertoService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

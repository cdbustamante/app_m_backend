package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.VlanSwitch;
import ec.gob.mtop.conexion.servicio.VlanSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vlans-switch")
public class VlanSwitchController {

    @Autowired
    private VlanSwitchService vlanSwitchService;

    /**
     * 📌 **Obtener una VlanSwitch por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<VlanSwitch> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(vlanSwitchService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todas las VlanSwitch activas**
     */
    @GetMapping
    public ResponseEntity<List<VlanSwitch>> listarTodos() {
        return ResponseEntity.ok(vlanSwitchService.listarTodos());
    }

    /**
     * 📌 **Crear una nueva VlanSwitch**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la crea.
     * 🔹 **Body:** Objeto `VlanSwitch`
     */
    @PostMapping
    public ResponseEntity<VlanSwitch> crear(@RequestBody VlanSwitch vlanSwitch, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(vlanSwitchService.crear(vlanSwitch, idUsuario));
    }

    /**
     * 📌 **Actualizar una VlanSwitch**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que la modifica.
     * 🔹 **Body:** Objeto `VlanSwitch`
     */
    @PutMapping("/{id}")
    public ResponseEntity<VlanSwitch> actualizar(@PathVariable Short id, @RequestBody VlanSwitch vlanSwitch, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(vlanSwitchService.actualizar(id, vlanSwitch, idUsuario));
    }

    /**
     * 📌 **Eliminar una VlanSwitch (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        vlanSwitchService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

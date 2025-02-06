package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.AccessPoint;
import ec.gob.mtop.conexion.servicio.AccessPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-points")
public class AccessPointController {

    @Autowired
    private AccessPointService accessPointService;

    /**
     * 📌 **Obtener un Access Point por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccessPoint> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(accessPointService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Access Points activos**
     */
    @GetMapping
    public ResponseEntity<List<AccessPoint>> listarTodos() {
        return ResponseEntity.ok(accessPointService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Access Point**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Parámetro:** `idPuerto` → ID del puerto al que se conecta el Access Point.
     * 🔹 **Body:** Objeto `AccessPoint`
     */
    @PostMapping
    public ResponseEntity<AccessPoint> crear(@RequestBody AccessPoint accessPoint, 
                                             @RequestParam Short idUsuario,
                                             @RequestParam(required = false) Short idPuerto) {
        return ResponseEntity.ok(accessPointService.crear(accessPoint, idUsuario, idPuerto));
    }

    /**
     * 📌 **Actualizar un Access Point**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Parámetro:** `idPuerto` → ID del puerto al que se conecta el Access Point.
     * 🔹 **Body:** Objeto `AccessPoint`
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccessPoint> actualizar(@PathVariable Short id, 
                                                  @RequestBody AccessPoint accessPoint, 
                                                  @RequestParam Short idUsuario,
                                                  @RequestParam(required = false) Short idPuerto) {
        return ResponseEntity.ok(accessPointService.actualizar(id, accessPoint, idUsuario, idPuerto));
    }

    /**
     * 📌 **Eliminar un Access Point (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        accessPointService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

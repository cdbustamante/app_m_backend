package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.servicio.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    /**
     * 📌 **Obtener un Equipo por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Equipos activos**
     */
    @GetMapping
    public ResponseEntity<List<Equipo>> listarTodos() {
        return ResponseEntity.ok(equipoService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Equipo**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Parámetro:** `idUsuarioEquipo` → ID del usuario asignado al equipo.
     * 🔹 **Parámetro:** `idTipoEquipo` → ID del tipo de equipo.
     * 🔹 **Body:** Objeto `Equipo`
     */
    @PostMapping
    public ResponseEntity<Equipo> crear(@RequestBody Equipo equipo, 
                                        @RequestParam Short idUsuario,
                                        @RequestParam(required = false) Short idUsuarioEquipo,
                                        @RequestParam Short idTipoEquipo) {
        return ResponseEntity.ok(equipoService.crear(equipo, idUsuario, idUsuarioEquipo, idTipoEquipo));
    }

    /**
     * 📌 **Actualizar un Equipo**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Parámetro:** `idUsuarioEquipo` → ID del usuario asignado al equipo.
     * 🔹 **Parámetro:** `idTipoEquipo` → ID del tipo de equipo.
     * 🔹 **Body:** Objeto `Equipo`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(@PathVariable Short id, 
                                             @RequestBody Equipo equipo, 
                                             @RequestParam Short idUsuario,
                                             @RequestParam(required = false) Short idUsuarioEquipo,
                                             @RequestParam Short idTipoEquipo) {
        return ResponseEntity.ok(equipoService.actualizar(id, equipo, idUsuario, idUsuarioEquipo, idTipoEquipo));
    }

    /**
     * 📌 **Eliminar un Equipo (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        equipoService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

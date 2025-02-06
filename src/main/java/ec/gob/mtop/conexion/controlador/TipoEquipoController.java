package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.TipoEquipo;
import ec.gob.mtop.conexion.servicio.TipoEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-equipo")
public class TipoEquipoController {

    @Autowired
    private TipoEquipoService tipoEquipoService;

    /**
     * 📌 **Obtener un Tipo de Equipo por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoEquipo> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(tipoEquipoService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Tipos de Equipo activos**
     */
    @GetMapping
    public ResponseEntity<List<TipoEquipo>> listarTodos() {
        return ResponseEntity.ok(tipoEquipoService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Tipo de Equipo**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Body:** Objeto `TipoEquipo`
     */
    @PostMapping
    public ResponseEntity<TipoEquipo> crear(@RequestBody TipoEquipo tipoEquipo, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(tipoEquipoService.crear(tipoEquipo, idUsuario));
    }

    /**
     * 📌 **Actualizar un Tipo de Equipo**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Body:** Objeto `TipoEquipo`
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoEquipo> actualizar(@PathVariable Short id, @RequestBody TipoEquipo tipoEquipo, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(tipoEquipoService.actualizar(id, tipoEquipo, idUsuario));
    }

    /**
     * 📌 **Eliminar un Tipo de Equipo (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        tipoEquipoService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

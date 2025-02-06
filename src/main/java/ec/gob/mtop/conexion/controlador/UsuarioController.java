package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * 📌 **Obtener un Usuario por ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Short id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /**
     * 📌 **Obtener todos los Usuarios activos**
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /**
     * 📌 **Crear un nuevo Usuario**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo crea.
     * 🔹 **Body:** Objeto `Usuario`
     */
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(usuarioService.crear(usuario, idUsuario));
    }

    /**
     * 📌 **Actualizar un Usuario**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que lo modifica.
     * 🔹 **Body:** Objeto `Usuario`
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Short id, @RequestBody Usuario usuario, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario, idUsuario));
    }

    /**
     * 📌 **Eliminar un Usuario (Desactivación lógica)**
     * 🔹 **Parámetro:** `idUsuario` → ID del usuario que realiza la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        usuarioService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

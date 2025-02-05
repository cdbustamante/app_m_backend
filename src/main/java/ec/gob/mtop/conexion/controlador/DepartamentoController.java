package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.servicio.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    /**
     * Obtener todos los departamentos activos
     */
    @GetMapping
    public ResponseEntity<List<Departamento>> listarTodos() {
        return ResponseEntity.ok(departamentoService.listarTodos());
    }

    /**
     * Obtener un departamento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> buscarPorId(@PathVariable Short id) {
        return ResponseEntity.ok(departamentoService.buscarPorId(id));
    }

    /**
     * Crear un nuevo departamento
     */
    @PostMapping
    public ResponseEntity<Departamento> crear(@RequestBody Departamento departamento, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(departamentoService.crear(departamento, idUsuario));
    }

    /**
     * Actualizar un departamento
     */
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizar(@PathVariable Short id, @RequestBody Departamento nuevosDatos, @RequestParam Short idUsuario) {
        return ResponseEntity.ok(departamentoService.actualizar(id, nuevosDatos, idUsuario));
    }

    /**
     * Eliminar un departamento (cambio de estado a inactivo)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Short id, @RequestParam Short idUsuario) {
        departamentoService.eliminar(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

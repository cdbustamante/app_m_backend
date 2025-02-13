package ec.gob.mtop.conexion.controlador;

import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.servicio.DepartamentoService;
import ec.gob.mtop.conexion.servicio.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private UsuarioService usuarioService;

    // 🔹 Obtener todos los departamentos activos
    @GetMapping
    public ResponseEntity<Page<Departamento>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String fechaCreacion,
            @RequestParam(required = false) String fechaModificacion,
            @RequestParam(required = false) String creador,
            @RequestParam(required = false) String modificador) {
                
                
        System.out.println("   Fecha Creación: " + fechaCreacion);


        Pageable pageable = PageRequest.of(page, size);
        Specification<Departamento> specs = departamentoService.crearEspecificacionFiltro(codigo, nombre, fechaCreacion, fechaModificacion, creador, modificador);

        Page<Departamento> departamentos = departamentoService.obtenerDepartamentosFiltrados(specs, pageable);
        return ResponseEntity.ok(departamentos);
    }

    // 🔹 Obtener un departamento por ID (solo si está activo)
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtenerPorId(@PathVariable Integer id) {
        return departamentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Crear un departamento (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Departamento> crearDepartamento(@RequestBody Departamento departamento) {
        // Obtener el usuario autenticado desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar el usuario en la base de datos para obtener su ID
        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD"));

        // Guardar el departamento con el ID del usuario autenticado
        Departamento nuevoDepartamento = departamentoService.crearDepartamento(departamento, usuario.getId().shortValue());
        return ResponseEntity.ok(nuevoDepartamento);
    }


    // 🔹 Actualizar un departamento (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizarDepartamento(
            @PathVariable Integer id,
            @RequestBody Departamento departamentoDetalles) {

        // 📌 Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD"));

        // 📌 Actualizar el departamento con el ID del usuario autenticado
        Departamento actualizado = departamentoService.actualizarDepartamento(id, departamentoDetalles, usuario.getId().shortValue());
        return ResponseEntity.ok(actualizado);
    }

    // 🔹 Borrado lógico del departamento (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDepartamento(@PathVariable Integer id) {
        // 📌 Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD"));

        // 📌 Borrar el departamento
        departamentoService.eliminarDepartamento(id, usuario.getId().shortValue());
        return ResponseEntity.noContent().build();
    }
}

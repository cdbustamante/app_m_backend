package ec.gob.mtop.conexion.servicio;

import ec.gob.mtop.conexion.excepcion.*;
import ec.gob.mtop.conexion.modelo.AccessPoint;
import ec.gob.mtop.conexion.modelo.Departamento;
import ec.gob.mtop.conexion.modelo.Equipo;
import ec.gob.mtop.conexion.modelo.Interfaz;
import ec.gob.mtop.conexion.modelo.Persona;
import ec.gob.mtop.conexion.modelo.Puerto;
import ec.gob.mtop.conexion.modelo.Rack;
import ec.gob.mtop.conexion.modelo.Switch;
import ec.gob.mtop.conexion.modelo.TipoEquipo;
import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.modelo.VlanSwitch;
import ec.gob.mtop.conexion.repositorio.AccessPointRepository;
import ec.gob.mtop.conexion.repositorio.DepartamentoRepository;
import ec.gob.mtop.conexion.repositorio.EquipoRepository;
import ec.gob.mtop.conexion.repositorio.InterfazRepository;
import ec.gob.mtop.conexion.repositorio.PersonaRepository;
import ec.gob.mtop.conexion.repositorio.PuertoRepository;
import ec.gob.mtop.conexion.repositorio.RackRepository;
import ec.gob.mtop.conexion.repositorio.SwitchRepository;
import ec.gob.mtop.conexion.repositorio.TipoEquipoRepository;
import ec.gob.mtop.conexion.repositorio.UsuarioRepository;
import ec.gob.mtop.conexion.repositorio.VlanSwitchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private InterfazRepository interfazRepository;
    @Autowired
    private PuertoRepository puertoRepository;
    @Autowired
    private SwitchRepository switchRepository;
    @Autowired
    private RackRepository rackRepository;
    @Autowired
    private VlanSwitchRepository vlanSwitchRepository;
    @Autowired
    private AccessPointRepository accessPointRepository;
    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;

    /**
     * Buscar un Usuario por ID si está activo
     */
    public Usuario buscarPorId(Short id) {
        try {
            return usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        } catch (Exception e) {
            throw new GetException("Error al obtener el usuario: " + e.getMessage());
        }
    }

    /**
     * Buscar un Usuario por su nombre de usuario si está activo (para autenticación)
     */
    public Usuario buscarPorUsername(String username) {
        try {
            return usuarioRepository.findByUsernameUsuarioAndRegistroActivoTrue(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        } catch (Exception e) {
            throw new GetException("Error al obtener el usuario por username: " + e.getMessage());
        }
    }

    /**
     * Listar todos los Usuarios activos
     */
    public List<Usuario> listarTodos() {
        try {
            return usuarioRepository.findByRegistroActivoTrue();
        } catch (Exception e) {
            throw new GetException("Error al listar los usuarios: " + e.getMessage());
        }
    }

    /**
     * Crear un nuevo Usuario, asignando el usuario creador
     */
    public Usuario crear(Usuario usuario, Short idUsuario) {
        try {
            usuario.setNombreUsuario(usuario.getNombreUsuario());
            usuario.setUsernameUsuario(usuario.getUsernameUsuario());
            usuario.setPasswordUsuario(usuario.getPasswordUsuario());
            usuario.setRolUsuario(usuario.getRolUsuario());
            usuario.setRegistroActivo(true);
            usuario.setCreadoPorUsuario(idUsuario);
            usuario.setFechaCreacion(LocalDateTime.now());

            // Los campos de modificación deben ser nulos en la creación
            usuario.setModificadoPorUsuario(null);
            usuario.setFechaModificacion(null);

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new InsertException("Error al insertar el usuario: " + e.getMessage());
        }
    }

    /**
     * Verificar si un Usuario existe y está activo
     */
    public boolean existePorId(Short id) {
        try {
            return usuarioRepository.existsByIdAndRegistroActivoTrue(id);
        } catch (Exception e) {
            throw new GetException("Error al verificar la existencia del usuario: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar un Usuario y aplicar cambios en auditoría a TODAS las entidades
     */
    public Usuario actualizar(Short id, Usuario nuevosDatos, Short idUsuario) {
        try {
            Usuario usuarioExistente = usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

            // 1️⃣ Desactivar el usuario actual
            usuarioExistente.setRegistroActivo(false);
            usuarioExistente.setModificadoPorUsuario(idUsuario);
            usuarioExistente.setFechaModificacion(LocalDateTime.now());
            usuarioRepository.save(usuarioExistente);

            // 2️⃣ Crear el nuevo usuario con los datos actualizados
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nuevosDatos.getNombreUsuario());
            nuevoUsuario.setUsernameUsuario(nuevosDatos.getUsernameUsuario());
            nuevoUsuario.setPasswordUsuario(nuevosDatos.getPasswordUsuario());
            nuevoUsuario.setRolUsuario(nuevosDatos.getRolUsuario());
            nuevoUsuario.setRegistroActivo(true);
            nuevoUsuario.setCreadoPorUsuario(idUsuario);
            nuevoUsuario.setFechaCreacion(LocalDateTime.now());

            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            // 3️⃣ Aplicar auditoría a TODAS las entidades
            actualizarAuditoria(id, usuarioGuardado.getId());

            return usuarioGuardado;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    /**
     * Eliminar un Usuario y desactivar TODOS los registros relacionados
     */
    public void eliminar(Short id, Short idUsuario) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .filter(Usuario::getRegistroActivo)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

            // 1️⃣ **Desactivar registros relacionados**
            desactivarRegistrosRelacionados(id);

            // 2️⃣ **Desactivar el Usuario**
            usuario.setRegistroActivo(false);
            usuario.setModificadoPorUsuario(idUsuario);
            usuario.setFechaModificacion(LocalDateTime.now());
            usuarioRepository.save(usuario);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    /**
     * **Actualizar los campos de auditoría en TODAS las entidades**
     */
    private void actualizarAuditoria(Short antiguoIdUsuario, Short nuevoIdUsuario) {
        try {
            // Actualizar en todas las entidades
            List<Departamento> departamentos = departamentoRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Departamento dep : departamentos) {
                if (dep.getCreadoPorUsuario().equals(antiguoIdUsuario)) dep.setCreadoPorUsuario(nuevoIdUsuario);
                if (dep.getModificadoPorUsuario() != null && dep.getModificadoPorUsuario().equals(antiguoIdUsuario)) dep.setModificadoPorUsuario(nuevoIdUsuario);
            }
            departamentoRepository.saveAll(departamentos);

            List<Persona> personas = personaRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Persona per : personas) {
                if (per.getCreadoPorUsuario().equals(antiguoIdUsuario)) per.setCreadoPorUsuario(nuevoIdUsuario);
                if (per.getModificadoPorUsuario() != null && per.getModificadoPorUsuario().equals(antiguoIdUsuario)) per.setModificadoPorUsuario(nuevoIdUsuario);
            }
            personaRepository.saveAll(personas);

            // 3️⃣ **Actualizar en la tabla `Equipos`**
            List<Equipo> equipos = equipoRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Equipo eq : equipos) {
                if (eq.getCreadoPorUsuario().equals(antiguoIdUsuario)) eq.setCreadoPorUsuario(nuevoIdUsuario);
                if (eq.getModificadoPorUsuario() != null && eq.getModificadoPorUsuario().equals(antiguoIdUsuario)) eq.setModificadoPorUsuario(nuevoIdUsuario);
            }
            equipoRepository.saveAll(equipos);

            // 4️⃣ **Actualizar en la tabla `Interfaz`**
            List<Interfaz> interfaces = interfazRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Interfaz inter : interfaces) {
                if (inter.getCreadoPorUsuario().equals(antiguoIdUsuario)) inter.setCreadoPorUsuario(nuevoIdUsuario);
                if (inter.getModificadoPorUsuario() != null && inter.getModificadoPorUsuario().equals(antiguoIdUsuario)) inter.setModificadoPorUsuario(nuevoIdUsuario);
            }
            interfazRepository.saveAll(interfaces);

            // 5️⃣ **Actualizar en la tabla `Puertos`**
            List<Puerto> puertos = puertoRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Puerto puerto : puertos) {
                if (puerto.getCreadoPorUsuario().equals(antiguoIdUsuario)) puerto.setCreadoPorUsuario(nuevoIdUsuario);
                if (puerto.getModificadoPorUsuario() != null && puerto.getModificadoPorUsuario().equals(antiguoIdUsuario)) puerto.setModificadoPorUsuario(nuevoIdUsuario);
            }
            puertoRepository.saveAll(puertos);

            // 6️⃣ **Actualizar en la tabla `Switches`**
            List<Switch> switches = switchRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Switch sw : switches) {
                if (sw.getCreadoPorUsuario().equals(antiguoIdUsuario)) sw.setCreadoPorUsuario(nuevoIdUsuario);
                if (sw.getModificadoPorUsuario() != null && sw.getModificadoPorUsuario().equals(antiguoIdUsuario)) sw.setModificadoPorUsuario(nuevoIdUsuario);
            }
            switchRepository.saveAll(switches);

            // 7️⃣ **Actualizar en la tabla `Racks`**
            List<Rack> racks = rackRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (Rack rack : racks) {
                if (rack.getCreadoPorUsuario().equals(antiguoIdUsuario)) rack.setCreadoPorUsuario(nuevoIdUsuario);
                if (rack.getModificadoPorUsuario() != null && rack.getModificadoPorUsuario().equals(antiguoIdUsuario)) rack.setModificadoPorUsuario(nuevoIdUsuario);
            }
            rackRepository.saveAll(racks);

            // 8️⃣ **Actualizar en la tabla `VlanSwitch`**
            List<VlanSwitch> vlans = vlanSwitchRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (VlanSwitch vlan : vlans) {
                if (vlan.getCreadoPorUsuario().equals(antiguoIdUsuario)) vlan.setCreadoPorUsuario(nuevoIdUsuario);
                if (vlan.getModificadoPorUsuario() != null && vlan.getModificadoPorUsuario().equals(antiguoIdUsuario)) vlan.setModificadoPorUsuario(nuevoIdUsuario);
            }
            vlanSwitchRepository.saveAll(vlans);
            
            List<TipoEquipo> tipoequipos = tipoEquipoRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (TipoEquipo tipoequipo : tipoequipos) {
                if (tipoequipo.getCreadoPorUsuario().equals(antiguoIdUsuario)) tipoequipo.setCreadoPorUsuario(nuevoIdUsuario);
                if (tipoequipo.getModificadoPorUsuario() != null && tipoequipo.getModificadoPorUsuario().equals(antiguoIdUsuario)) tipoequipo.setModificadoPorUsuario(nuevoIdUsuario);
            }
            tipoEquipoRepository.saveAll(tipoequipos);
            
            List<AccessPoint> accesspoints = accessPointRepository.findByCreadoPorUsuarioOrModificadoPorUsuario(antiguoIdUsuario, antiguoIdUsuario);
            for (AccessPoint accesspoint : accesspoints) {
                if (accesspoint.getCreadoPorUsuario().equals(antiguoIdUsuario)) accesspoint.setCreadoPorUsuario(nuevoIdUsuario);
                if (accesspoint.getModificadoPorUsuario() != null && accesspoint.getModificadoPorUsuario().equals(antiguoIdUsuario)) accesspoint.setModificadoPorUsuario(nuevoIdUsuario);
            }
            accessPointRepository.saveAll(accesspoints);
            
        } catch (Exception e) {
            throw new UpdateException("Error al actualizar auditoría en todas las entidades: " + e.getMessage());
        }
    }

    /**
     * **Desactivar TODOS los registros relacionados en TODAS las entidades**
     */
    private void desactivarRegistrosRelacionados(Short idUsuario) {
        try {
            List<Persona> personas = personaRepository.findByRegistroActivoTrue();
            for (Persona persona : personas) {
                if (persona.getCreadoPorUsuario().equals(idUsuario) || (persona.getModificadoPorUsuario() != null && persona.getModificadoPorUsuario().equals(idUsuario))) {
                    persona.setRegistroActivo(false);
                    persona.setModificadoPorUsuario(idUsuario);
                    persona.setFechaModificacion(LocalDateTime.now());
                }
            }
            personaRepository.saveAll(personas);

            List<Equipo> equipos = equipoRepository.findByRegistroActivoTrue();
            for (Equipo equipo : equipos) {
                if (equipo.getCreadoPorUsuario().equals(idUsuario) || (equipo.getModificadoPorUsuario() != null && equipo.getModificadoPorUsuario().equals(idUsuario))) {
                    equipo.setRegistroActivo(false);
                    equipo.setModificadoPorUsuario(idUsuario);
                    equipo.setFechaModificacion(LocalDateTime.now());
                }
            }
            equipoRepository.saveAll(equipos);

         // 3️⃣ **Desactivar en la tabla `Interfaz`**
            List<Interfaz> interfaces = interfazRepository.findByRegistroActivoTrue();
            for (Interfaz inter : interfaces) {
                if (inter.getCreadoPorUsuario().equals(idUsuario) || 
                    (inter.getModificadoPorUsuario() != null && inter.getModificadoPorUsuario().equals(idUsuario))) {
                    inter.setRegistroActivo(false);
                    inter.setModificadoPorUsuario(idUsuario);
                    inter.setFechaModificacion(LocalDateTime.now());
                }
            }
            interfazRepository.saveAll(interfaces);

            // 4️⃣ **Desactivar en la tabla `Puertos`**
            List<Puerto> puertos = puertoRepository.findByRegistroActivoTrue();
            for (Puerto puerto : puertos) {
                if (puerto.getCreadoPorUsuario().equals(idUsuario) || 
                    (puerto.getModificadoPorUsuario() != null && puerto.getModificadoPorUsuario().equals(idUsuario))) {
                    puerto.setRegistroActivo(false);
                    puerto.setModificadoPorUsuario(idUsuario);
                    puerto.setFechaModificacion(LocalDateTime.now());
                }
            }
            puertoRepository.saveAll(puertos);

            // 5️⃣ **Desactivar en la tabla `Switches`**
            List<Switch> switches = switchRepository.findByRegistroActivoTrue();
            for (Switch sw : switches) {
                if (sw.getCreadoPorUsuario().equals(idUsuario) || 
                    (sw.getModificadoPorUsuario() != null && sw.getModificadoPorUsuario().equals(idUsuario))) {
                    sw.setRegistroActivo(false);
                    sw.setModificadoPorUsuario(idUsuario);
                    sw.setFechaModificacion(LocalDateTime.now());
                }
            }
            switchRepository.saveAll(switches);

            // 6️⃣ **Desactivar en la tabla `Racks`**
            List<Rack> racks = rackRepository.findByRegistroActivoTrue();
            for (Rack rack : racks) {
                if (rack.getCreadoPorUsuario().equals(idUsuario) || 
                    (rack.getModificadoPorUsuario() != null && rack.getModificadoPorUsuario().equals(idUsuario))) {
                    rack.setRegistroActivo(false);
                    rack.setModificadoPorUsuario(idUsuario);
                    rack.setFechaModificacion(LocalDateTime.now());
                }
            }
            rackRepository.saveAll(racks);

            // 7️⃣ **Desactivar en la tabla `VlanSwitch`**
            List<VlanSwitch> vlans = vlanSwitchRepository.findByRegistroActivoTrue();
            for (VlanSwitch vlan : vlans) {
                if (vlan.getCreadoPorUsuario().equals(idUsuario) || 
                    (vlan.getModificadoPorUsuario() != null && vlan.getModificadoPorUsuario().equals(idUsuario))) {
                    vlan.setRegistroActivo(false);
                    vlan.setModificadoPorUsuario(idUsuario);
                    vlan.setFechaModificacion(LocalDateTime.now());
                }
            }
            vlanSwitchRepository.saveAll(vlans);

            // 8️⃣ **Desactivar en la tabla `TipoEquipo`** (Nuevo)
            List<TipoEquipo> tiposEquipo = tipoEquipoRepository.findByRegistroActivoTrue();
            for (TipoEquipo tipo : tiposEquipo) {
                if (tipo.getCreadoPorUsuario().equals(idUsuario) || 
                    (tipo.getModificadoPorUsuario() != null && tipo.getModificadoPorUsuario().equals(idUsuario))) {
                    tipo.setRegistroActivo(false);
                    tipo.setModificadoPorUsuario(idUsuario);
                    tipo.setFechaModificacion(LocalDateTime.now());
                }
            }
            tipoEquipoRepository.saveAll(tiposEquipo);

            // 9️⃣ **Desactivar en la tabla `AccessPoint`** (Nuevo)
            List<AccessPoint> accessPoints = accessPointRepository.findByRegistroActivoTrue();
            for (AccessPoint ap : accessPoints) {
                if (ap.getCreadoPorUsuario().equals(idUsuario) || 
                    (ap.getModificadoPorUsuario() != null && ap.getModificadoPorUsuario().equals(idUsuario))) {
                    ap.setRegistroActivo(false);
                    ap.setModificadoPorUsuario(idUsuario);
                    ap.setFechaModificacion(LocalDateTime.now());
                }
            }
            accessPointRepository.saveAll(accessPoints);
            
            List<Departamento> departamentos = departamentoRepository.findByRegistroActivoTrue();
            for (Departamento dep : departamentos) {
                if (dep.getCreadoPorUsuario().equals(idUsuario) || 
                    (dep.getModificadoPorUsuario() != null && dep.getModificadoPorUsuario().equals(idUsuario))) {
                    dep.setRegistroActivo(false);
                    dep.setModificadoPorUsuario(idUsuario);
                    dep.setFechaModificacion(LocalDateTime.now());
                }
            }
            departamentoRepository.saveAll(departamentos);
            
        } catch (Exception e) {
            throw new UpdateException("Error al desactivar registros relacionados: " + e.getMessage());
        }
    }

}

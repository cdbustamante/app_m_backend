package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos", schema = "esquema_conexiones")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Integer id;

    @Column(name = "nombre_equipo", nullable = false)
    private String nombreEquipo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_equipo", referencedColumnName = "id_per", foreignKey = @ForeignKey(name = "fk_id_usuario_equipo"))
    private Persona usuarioEquipo;

    @ManyToOne
    @JoinColumn(name = "id_tipo_equipo", referencedColumnName = "id_tipo", foreignKey = @ForeignKey(name = "fk_id_tipo_equipo"), nullable = false)
    private TipoEquipo tipoEquipo;

    @Column(name = "registro_activo", nullable = false)
    private Boolean registroActivo = true;

    @Column(name = "creado_por_usuario", nullable = false)
    private Short creadoPorUsuario;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "modificado_por_usuario")
    private Short modificadoPorUsuario;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // Getters y Setters
    public Integer getId() {
        return id;
    }


    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Persona getUsuarioEquipo() {
        return usuarioEquipo;
    }

    public void setUsuarioEquipo(Persona usuarioEquipo) {
        this.usuarioEquipo = usuarioEquipo;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(TipoEquipo tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public Boolean getRegistroActivo() {
        return registroActivo;
    }

    public void setRegistroActivo(Boolean registroActivo) {
        this.registroActivo = registroActivo;
    }

    public Short getCreadoPorUsuario() {
        return creadoPorUsuario;
    }

    public void setCreadoPorUsuario(Short creadoPorUsuario) {
        this.creadoPorUsuario = creadoPorUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Short getModificadoPorUsuario() {
        return modificadoPorUsuario;
    }

    public void setModificadoPorUsuario(Short modificadoPorUsuario) {
        this.modificadoPorUsuario = modificadoPorUsuario;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}

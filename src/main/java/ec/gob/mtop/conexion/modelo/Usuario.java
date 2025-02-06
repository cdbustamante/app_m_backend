package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", schema = "esquema_conexiones")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false)
    private String nombreUsuario;

    @Column(name = "username_usuario", nullable = false, unique = true)
    private String usernameUsuario;

    @Column(name = "password_usuario", nullable = false)
    private String passwordUsuario;

    @Column(name = "rol_usuario", nullable = false)
    private String rolUsuario;

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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getUsernameUsuario() {
        return usernameUsuario;
    }

    public void setUsernameUsuario(String usernameUsuario) {
        this.usernameUsuario = usernameUsuario;
    }

    public String getPasswordUsuario() {
        return passwordUsuario;
    }

    public void setPasswordUsuario(String passwordUsuario) {
        this.passwordUsuario = passwordUsuario;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
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


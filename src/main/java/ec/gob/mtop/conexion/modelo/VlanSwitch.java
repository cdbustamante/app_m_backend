package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vlans_switch", schema = "esquema_conexiones")
public class VlanSwitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vlan_sw")
    private Integer id;

    @Column(name = "nombre_vlan", nullable = false)
    private String nombreVlan;

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

    public String getNombreVlan() {
        return nombreVlan;
    }

    public void setNombreVlan(String nombreVlan) {
        this.nombreVlan = nombreVlan;
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


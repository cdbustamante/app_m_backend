package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interfaces", schema = "esquema_conexiones")
public class Interfaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_iface")
    private Short id;

    @Column(name = "mac_iface", nullable = false, unique = true)
    private String macIface;

    @Column(name = "ip_iface", nullable = false, unique = true)
    private String ipIface; // IPv4 como String

    @ManyToOne
    @JoinColumn(name = "id_puerto_iface", referencedColumnName = "id_puerto", foreignKey = @ForeignKey(name = "fk_id_puerto_iface"))
    private Puerto puerto;

    @ManyToOne
    @JoinColumn(name = "id_equipo_iface", referencedColumnName = "id_equipo", foreignKey = @ForeignKey(name = "fk_id_equipo_iface"))
    private Equipo equipo;

    @Column(name = "tipo_iface", nullable = false)
    private String tipoIface;

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
    public Short getId() {
        return id;
    }

    public String getMacIface() {
        return macIface;
    }

    public void setMacIface(String macIface) {
        this.macIface = macIface;
    }

    public String getIpIface() {
        return ipIface;
    }

    public void setIpIface(String ipIface) {
        this.ipIface = ipIface;
    }

    public Puerto getPuerto() {
        return puerto;
    }

    public void setPuerto(Puerto puerto) {
        this.puerto = puerto;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getTipoIface() {
        return tipoIface;
    }

    public void setTipoIface(String tipoIface) {
        this.tipoIface = tipoIface;
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


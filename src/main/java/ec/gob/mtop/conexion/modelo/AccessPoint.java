package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_point", schema = "esquema_conexiones")
public class AccessPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ap")
    private Short id;

    @Column(name = "nombre_ap", nullable = false)
    private String nombreAp;

    @Column(name = "modelo_ap", nullable = false)
    private String modeloAp;

    @Column(name = "mac_eth_ap", nullable = false, unique = true)
    private String macEthAp;

    @Column(name = "ip_ap", nullable = false, unique = true)
    private String ipAp; // IPv4 como String

    @ManyToOne
    @JoinColumn(name = "id_puerto_ap", referencedColumnName = "id_puerto", foreignKey = @ForeignKey(name = "fk_id_puerto_ap"))
    private Puerto puerto;

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

    public void setId(Short id) {
        this.id = id;
    }

    public String getNombreAp() {
        return nombreAp;
    }

    public void setNombreAp(String nombreAp) {
        this.nombreAp = nombreAp;
    }

    public String getModeloAp() {
        return modeloAp;
    }

    public void setModeloAp(String modeloAp) {
        this.modeloAp = modeloAp;
    }

    public String getMacEthAp() {
        return macEthAp;
    }

    public void setMacEthAp(String macEthAp) {
        this.macEthAp = macEthAp;
    }

    public String getIpAp() {
        return ipAp;
    }

    public void setIpAp(String ipAp) {
        this.ipAp = ipAp;
    }

    public Puerto getPuerto() {
        return puerto;
    }

    public void setPuerto(Puerto puerto) {
        this.puerto = puerto;
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


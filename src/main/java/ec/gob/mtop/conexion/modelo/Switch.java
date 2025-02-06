package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "switches", schema = "esquema_conexiones")
public class Switch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sw")
    private Integer id;

    @Column(name = "nombre_sw", nullable = false)
    private String nombreSw;

    @Column(name = "modelo_sw", nullable = false)
    private String modeloSw;

    @Column(name = "mac_eth_sw", nullable = false, unique = true)
    private String macEthSw;

    @Column(name = "ip_sw", nullable = false, unique = true)
    private String ipSw; // IPv4 como String

    @ManyToOne
    @JoinColumn(name = "id_racks_sw", referencedColumnName = "id_racks", foreignKey = @ForeignKey(name = "fk_id_racks_sw"))
    private Rack rack;

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


    public String getNombreSw() {
        return nombreSw;
    }

    public void setNombreSw(String nombreSw) {
        this.nombreSw = nombreSw;
    }

    public String getModeloSw() {
        return modeloSw;
    }

    public void setModeloSw(String modeloSw) {
        this.modeloSw = modeloSw;
    }

    public String getMacEthSw() {
        return macEthSw;
    }

    public void setMacEthSw(String macEthSw) {
        this.macEthSw = macEthSw;
    }

    public String getIpSw() {
        return ipSw;
    }

    public void setIpSw(String ipSw) {
        this.ipSw = ipSw;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
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

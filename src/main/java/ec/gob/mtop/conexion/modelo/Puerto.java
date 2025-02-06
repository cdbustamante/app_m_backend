package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "puertos", schema = "esquema_conexiones")
public class Puerto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puerto")
    private Integer id;

    @Column(name = "nombre_puerto", nullable = false)
    private String nombrePuerto;

    @Column(name = "etiqueta_puerto")
    private String etiquetaPuerto;

    @Column(name = "estado_puerto", nullable = false)
    private Boolean estadoPuerto = true;

    @ManyToOne
    @JoinColumn(name = "id_vlan_sw_puerto", referencedColumnName = "id_vlan_sw", foreignKey = @ForeignKey(name = "fk_id_vlan_sw_puerto"))
    private VlanSwitch vlanSwitch;

    @ManyToOne
    @JoinColumn(name = "id_sw_puerto", referencedColumnName = "id_sw", foreignKey = @ForeignKey(name = "fk_id_sw_puerto"), nullable = false)
    private Switch switchRed;

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

    public String getNombrePuerto() {
        return nombrePuerto;
    }

    public void setNombrePuerto(String nombrePuerto) {
        this.nombrePuerto = nombrePuerto;
    }

    public String getEtiquetaPuerto() {
        return etiquetaPuerto;
    }

    public void setEtiquetaPuerto(String etiquetaPuerto) {
        this.etiquetaPuerto = etiquetaPuerto;
    }

    public Boolean getEstadoPuerto() {
        return estadoPuerto;
    }

    public void setEstadoPuerto(Boolean estadoPuerto) {
        this.estadoPuerto = estadoPuerto;
    }

    public VlanSwitch getVlanSwitch() {
        return vlanSwitch;
    }

    public void setVlanSwitch(VlanSwitch vlanSwitch) {
        this.vlanSwitch = vlanSwitch;
    }

    public Switch getSwitchRed() {
        return switchRed;
    }

    public void setSwitchRed(Switch switchRed) {
        this.switchRed = switchRed;
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

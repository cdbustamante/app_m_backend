package ec.gob.mtop.conexion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personas", schema = "esquema_conexiones")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_per")
    private Integer id;

    @Column(name = "cedula_per", nullable = false, unique = true)
    private String cedula;

    @Column(name = "nombres_per", nullable = false)
    private String nombres;

    @Column(name = "alias_per")
    private String alias;

    @ManyToOne
    @JoinColumn(name = "id_depar_per", referencedColumnName = "id_depar", foreignKey = @ForeignKey(name = "fk_id_depar_usuario"))
    private Departamento departamento;

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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
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

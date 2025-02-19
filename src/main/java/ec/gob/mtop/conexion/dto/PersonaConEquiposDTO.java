package ec.gob.mtop.conexion.dto;

import java.util.List;
import java.util.Map;

public class PersonaConEquiposDTO {
    private String cedula;
    private String nombres;
    private String alias;
    private Map<String, Map<String, List<EquipoInfoDTO>>> equiposPorConexion;
    public PersonaConEquiposDTO() {}

    public PersonaConEquiposDTO(String cedula, String nombres, String alias, Map<String, Map<String, List<EquipoInfoDTO>>> equiposPorConexion) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.alias = alias;
        this.equiposPorConexion = equiposPorConexion;
    }

    // Getters y Setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public Map<String, Map<String, List<EquipoInfoDTO>>> getEquiposPorConexion() { return equiposPorConexion; }
    public void setEquiposPorConexion(Map<String, Map<String, List<EquipoInfoDTO>>> equiposPorConexion) { this.equiposPorConexion = equiposPorConexion; }
}

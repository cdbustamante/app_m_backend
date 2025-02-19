package ec.gob.mtop.conexion.dto;

public class EquipoInfoDTO {
    private String nombre;
    private String ip;
    private String mac;

    public EquipoInfoDTO(String nombre, String ip, String mac) {
        this.nombre = nombre;
        this.ip = ip;
        this.mac = mac;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }
}

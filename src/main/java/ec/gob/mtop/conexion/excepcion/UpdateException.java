package ec.gob.mtop.conexion.excepcion;

public class UpdateException extends RuntimeException {
    public UpdateException(String mensaje) {
        super(mensaje);
    }
}
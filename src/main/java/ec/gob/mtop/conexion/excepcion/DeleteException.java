package ec.gob.mtop.conexion.excepcion;

public class DeleteException extends RuntimeException {
    public DeleteException(String mensaje) {
        super(mensaje);
    }
}

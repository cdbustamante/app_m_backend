package ec.gob.mtop.conexion.excepcion;

public class InsertException extends RuntimeException {
    public InsertException(String mensaje) {
        super(mensaje);
    }
}

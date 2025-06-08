package api.Exception;

public class CambioEstadoSolicitudException extends RuntimeException {
    public CambioEstadoSolicitudException() {
        super("No se puede aceptar o rechazar una solicitud que no esta en espera");
    }
}

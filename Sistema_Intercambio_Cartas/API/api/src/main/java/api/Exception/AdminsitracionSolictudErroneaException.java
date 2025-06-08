package api.Exception;

public class AdminsitracionSolictudErroneaException extends RuntimeException {
    public AdminsitracionSolictudErroneaException() {
        super("El parametro de estado de administracion de solicitud es erroneo");
    }
}

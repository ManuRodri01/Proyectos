package api.Exception;

public class LimiteDeSolicitudesException extends RuntimeException {
    public LimiteDeSolicitudesException(String tipo) {
        super("Se ha alcanzado el limite diario de solicitudes de creacion de "+tipo);
    }
}

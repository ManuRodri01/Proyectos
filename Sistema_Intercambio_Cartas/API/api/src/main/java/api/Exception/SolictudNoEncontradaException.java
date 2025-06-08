package api.Exception;

public class SolictudNoEncontradaException extends RuntimeException {
    public SolictudNoEncontradaException(String id) {
        super("La solicitud de id: " + id + " no ha sido encontrada");
    }
}

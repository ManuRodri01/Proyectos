package api.Exception;

public class OfertaNoEncontradaException extends RuntimeException {
    public OfertaNoEncontradaException(String id) {
        super("Oferta no encontrada, id: " + id);
    }
}

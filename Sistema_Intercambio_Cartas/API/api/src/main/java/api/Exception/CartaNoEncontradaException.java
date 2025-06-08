package api.Exception;

public class CartaNoEncontradaException extends RuntimeException {
    public CartaNoEncontradaException(String id) {
        super("No se encontro la carta con id: " + id);
    }
}

package grupo1.tpTACS_API.Exception;

public class CartaNoEncontradaException extends RuntimeException {
    public CartaNoEncontradaException(int id) {
        super("No se encontro la carta con id: " + id);
    }
}

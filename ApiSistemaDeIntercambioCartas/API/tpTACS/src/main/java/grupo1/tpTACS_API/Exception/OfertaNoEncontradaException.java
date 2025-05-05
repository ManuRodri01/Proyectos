package grupo1.tpTACS_API.Exception;

public class OfertaNoEncontradaException extends RuntimeException {
    public OfertaNoEncontradaException(int id) {
        super("Oferta no encontrada, id: " + id);
    }
}

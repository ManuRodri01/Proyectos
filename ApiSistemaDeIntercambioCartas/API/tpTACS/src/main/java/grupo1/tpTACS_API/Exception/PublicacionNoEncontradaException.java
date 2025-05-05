package grupo1.tpTACS_API.Exception;

public class PublicacionNoEncontradaException extends RuntimeException {
    public PublicacionNoEncontradaException(int id) {
        super("Publicacion no encontrada, id: " + id);
    }
}
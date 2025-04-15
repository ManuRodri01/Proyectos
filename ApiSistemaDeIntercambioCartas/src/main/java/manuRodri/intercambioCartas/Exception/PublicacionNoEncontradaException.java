package manuRodri.intercambioCartas.Exception;

public class PublicacionNoEncontradaException extends RuntimeException {
    public PublicacionNoEncontradaException(int id) {
        super("Publicacion no encontrada, id: " + id);
    }
}
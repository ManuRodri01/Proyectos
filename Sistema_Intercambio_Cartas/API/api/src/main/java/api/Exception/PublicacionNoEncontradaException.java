package api.Exception;

import org.springframework.http.HttpStatus;

public class PublicacionNoEncontradaException extends CartamaniaException {
    private final String publicacionId;

    public PublicacionNoEncontradaException(String publicacionId) {
        super("La publicación con ID " + publicacionId + " no existe.");
        this.publicacionId = publicacionId;
    }

    @Override
    public String getCode() {
        return "PUBLICACION_NO_EXISTE";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
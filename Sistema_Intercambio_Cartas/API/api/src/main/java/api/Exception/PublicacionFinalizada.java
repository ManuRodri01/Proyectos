package api.Exception;

import org.springframework.http.HttpStatus;

public class PublicacionFinalizada extends CartamaniaException {
    public PublicacionFinalizada(String publicacionId) {
        super("La publicacion con id " + publicacionId + " ha sido finalizada.");
    }

    @Override
    public String getCode() {
        return "PUBLICACION_FINALIZADA";
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.GONE;
    }
}

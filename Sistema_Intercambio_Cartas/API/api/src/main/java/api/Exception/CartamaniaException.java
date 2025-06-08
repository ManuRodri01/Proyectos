package api.Exception;

import org.springframework.http.HttpStatus;

public abstract class CartamaniaException extends RuntimeException {
    public CartamaniaException(String message) {
        super(message);
    }
    /**
     * Código único de error que el frontend puede usar para tomar decisiones.
     * Ej: "PUBLICACION_NO_EXISTE", "OFERTA_INVALIDA"
     */
    public abstract String getCode();

    /**
     * El status HTTP que se debe devolver al cliente (por ejemplo 404, 409, etc).
     */
    public abstract HttpStatus getStatus();
}

package grupo1.tpTACS_API.Exception;

public class CartaYaExistenteException extends RuntimeException {
    public CartaYaExistenteException(String nombre) {
        super("La carta: " + nombre + " ya existe en el sistema");
    }
}

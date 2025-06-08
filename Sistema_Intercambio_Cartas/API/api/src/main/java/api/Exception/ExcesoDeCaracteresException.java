package api.Exception;

public class ExcesoDeCaracteresException extends RuntimeException {
    public ExcesoDeCaracteresException(String campo, int caracteresMaximos) {
        super("Error, en " + campo + ". El maximo de caracteres es: " + caracteresMaximos);
    }
}

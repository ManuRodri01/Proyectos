package api.Exception;

public class JuegoYaExistenteException extends RuntimeException {
  public JuegoYaExistenteException(String nombre) {
    super("El juego: " + nombre + " ya existe.");
  }
}

package api.Exception;

public class JuegoNoEncontradoException extends RuntimeException {
  public JuegoNoEncontradoException(String id) {
    super("No se encuentra el juego de id: " + id);
  }
}

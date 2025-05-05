package grupo1.tpTACS_API.Exception;

public class JuegoNoEncontradoException extends RuntimeException {
  public JuegoNoEncontradoException(int id) {
    super("No se encuentra el juego de id: " + id);
  }
}

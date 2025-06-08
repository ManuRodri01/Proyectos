package api.Exception;

public class PaginaInexistenteException extends RuntimeException {
  public PaginaInexistenteException(int pagina) {
    super("La pagina " + pagina + " no existe");
  }
}

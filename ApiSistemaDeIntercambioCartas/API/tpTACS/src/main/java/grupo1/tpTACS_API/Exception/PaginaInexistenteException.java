package grupo1.tpTACS_API.Exception;

public class PaginaInexistenteException extends RuntimeException {
  public PaginaInexistenteException(int pagina) {
    super("La pagina " + pagina + " no existe");
  }
}

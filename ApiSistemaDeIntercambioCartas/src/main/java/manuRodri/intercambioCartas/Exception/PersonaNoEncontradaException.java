package manuRodri.intercambioCartas.Exception;

public class PersonaNoEncontradaException extends RuntimeException {
  public PersonaNoEncontradaException(String nombreUsuario) {
    super("No se encontro a la persona de nombre: " + nombreUsuario);
  }
}

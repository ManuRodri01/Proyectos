package manuRodri.intercambioCartas.Exception;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String nombreUsuario) {
        super("El nombre de usuario '" + nombreUsuario + "' ya está en uso.");
    }
}

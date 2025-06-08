package bot.Excepciones;

public class UsuarioYaExisenteExcepcion extends RuntimeException {
    public UsuarioYaExisenteExcepcion(String usuario) {
        super("El nombre del usuario ya existe: " + usuario);
    }
}

package manuRodri.intercambioCartas.Exception;

public class PersonaSinPermisosException extends RuntimeException {
    public PersonaSinPermisosException(String nombre) {
        super("La persona: " + nombre + " no tiene los permisos necesarios ");
    }
}

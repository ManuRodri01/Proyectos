package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerGlobal {

    /*
        Con esta clase se van a poder manejar errores de forma global,
        por ejemplo se pueden agregar aca las publicaciones no encontradas
        y las ofertas no encontradas.
     */

    @ExceptionHandler(PersonaNoEncontradaException.class)
    public ResponseEntity<String> manejarPersonaNoEncontradaException(PersonaNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        //Si en algun momento de la ejecucion, se lanza una excepcion del tipo PersonaNoEncontrada
        //este metodo se va a encargar de dar la respuesta con el codigo HTTP de Not Found
    }

    @ExceptionHandler(PersonaSinPermisosException.class)
    public ResponseEntity<String> manejarPersonaSinPermisosException(PersonaSinPermisosException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        //Si en algun momento de la ejecucion, se lanza una excepcion del tipo PersonaSinPermisos
        //este metodo se va a encargar de dar la respuesta con el codigo HTTP de Forbidden
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<String> manejarUsuarioYaExiste(UsuarioYaExisteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PublicacionNoEncontradaException.class)
    public ResponseEntity<String> manejarPublicacionNoEncontrada(PublicacionNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartaNoEncontradaException.class)
    public ResponseEntity<String> manejarCartaNoEncontrada(CartaNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OfertaNoEncontradaException.class)
    public ResponseEntity<String> manejarOfertaNoEncontrada(OfertaNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


}

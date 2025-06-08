package api.Controller;

import api.DTO.ErrorResponse;
import api.Exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

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

    @ExceptionHandler(CartaNoEncontradaException.class)
    public ResponseEntity<String> manejarCartaNoEncontrada(CartaNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JuegoNoEncontradoException.class)
    public ResponseEntity<String> manejarJuegoNoEncontrado(JuegoNoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SolictudNoEncontradaException.class)
    public ResponseEntity<String> manejarSolicitudNoEncontrada(SolictudNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartaYaExistenteException.class)
    public ResponseEntity<String> manejarCartaYaExistente(CartaYaExistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JuegoYaExistenteException.class)
    public ResponseEntity<String> manejarJuegoYaExistente(JuegoYaExistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OfertaNoEncontradaException.class)
    public ResponseEntity<String> manejarOfertaNoEncontrada(OfertaNoEncontradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaginaInexistenteException.class)
    public ResponseEntity<String> manejarPaginaInexistente(PaginaInexistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
  
    @ExceptionHandler(ExcesoDeCaracteresException.class)
    public ResponseEntity<String> manejarExcesoCaracteres(ExcesoDeCaracteresException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(LimiteDeSolicitudesException.class)
    public ResponseEntity<String> manejarLimiteDeSolicitudes(LimiteDeSolicitudesException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(AdminsitracionSolictudErroneaException.class)
    public ResponseEntity<String> manejarAdminsitracionSolicitudErronea(AdminsitracionSolictudErroneaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CambioEstadoSolicitudException.class)
    public ResponseEntity<String> manejarCambioEstadoSolicitud(CambioEstadoSolicitudException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
  
     @ExceptionHandler(CartamaniaException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(CartamaniaException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                ex.getCode(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }


}

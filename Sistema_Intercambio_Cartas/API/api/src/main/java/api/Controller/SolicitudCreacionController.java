package api.Controller;

import api.DTO.*;
import api.Entity.Persona;
import api.Exception.AdminsitracionSolictudErroneaException;
import api.Service.SolicitudCartaService;
import api.Service.SolicitudJuegoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SolicitudCreacionController {
    private final SolicitudJuegoService solicitudJuegoService;

    private final SolicitudCartaService solicitudCartaService;


    public SolicitudCreacionController(SolicitudJuegoService solicitudJuegoService, SolicitudCartaService solicitudCartaService) {
        this.solicitudJuegoService = solicitudJuegoService;
        this.solicitudCartaService = solicitudCartaService;
    }

    @PostMapping("/solicitudes/juegos")
    public void crearSolicitudJuego(@RequestBody CreacionSolicitudJuegoDTO creacionSolicitud, @AuthenticationPrincipal Persona persona) {
        solicitudJuegoService.guardarSolicitud(creacionSolicitud,persona);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/solicitudes/juegos")
    public List<SolicitudJuegoDTO> obtenerSolicitudJuegos() {
        return solicitudJuegoService.obtenerTodasSolicitudes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/solicitudes/juegos/en_espera")
    public List<SolicitudJuegoDTO> obtenerSolicitudJuegosEnEspera() {
        return solicitudJuegoService.obtenerTodasSolicitudesEnEspera();
    }

    @GetMapping("/solicitudes/juegos/persona")
    public List<SolicitudJuegoDTO> obtenerSolicitudesJuegoByPersona(@AuthenticationPrincipal Persona persona) {
        return solicitudJuegoService.obtenerSolicitudesBySolicitante(persona);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/solicitudes/juegos/{solicitud-id}/estado-aprobacion")
    public void administrarSolictudJuego(@RequestBody EstadoSolicitudDTO estadoSolicitud, @PathVariable("solicitud-id") String id) {
        switch (estadoSolicitud.estado()){
            case APROBADA:
                solicitudJuegoService.aceptarSolicitud(id);
                break;
            case RECHAZADA:
                solicitudJuegoService.rechazarSolicitud(id);
                break;
            default:
                throw new AdminsitracionSolictudErroneaException();
        }
    }

    @GetMapping("/solicitudes/juegos/{id-solicitud}")
    public SolicitudJuegoDTO obtenerSolicitudJuego(@PathVariable("id-solicitud") String id, @AuthenticationPrincipal Persona persona) {
        return solicitudJuegoService.obtenerSolicitud(id, persona);
    }





    @PostMapping("/solicitudes/cartas")
    public void crearSolicitudCarta(@RequestBody CreacionSolicitudCartaDTO creacionSolicitud, @AuthenticationPrincipal Persona persona) {
        solicitudCartaService.guardarSolicitud(creacionSolicitud,persona);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/solicitudes/cartas")
    public List<SolicitudCartaDTO> obtenerSolicitudCartas() {
        return solicitudCartaService.obtenerTodasSolicitudes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/solicitudes/cartas/en_espera")
    public List<SolicitudCartaDTO> obtenerSolicitudCartasEnEspera() {
        return solicitudCartaService.obtenerTodasSolicitudesEnEspera();
    }

    @GetMapping("/solicitudes/cartas/persona")
    public List<SolicitudCartaDTO> obtenerSolicitudesCartaByPersona(@AuthenticationPrincipal Persona persona) {
        return solicitudCartaService.obtenerSolicitudesBySolicitante(persona);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/solicitudes/cartas/{solicitud-id}/estado-aprobacion")
    public void administrarSolictudCarta(@RequestBody EstadoSolicitudDTO estadoSolicitud, @PathVariable("solicitud-id") String id) {
        switch (estadoSolicitud.estado()){
            case APROBADA:
                solicitudCartaService.aceptarSolicitud(id);
                break;
            case RECHAZADA:
                solicitudCartaService.rechazarSolicitud(id);
                break;
            default:
                throw new AdminsitracionSolictudErroneaException();
        }
    }

    @GetMapping("/solicitudes/cartas/{id-solicitud}")
    public SolicitudCartaDTO obtenerSolicitudCarta(@PathVariable("id-solicitud") String id, @AuthenticationPrincipal Persona persona) {
        return solicitudCartaService.obtenerSolicitud(id, persona);
    }
}

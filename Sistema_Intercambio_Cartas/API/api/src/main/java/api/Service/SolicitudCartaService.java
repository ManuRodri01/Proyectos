package api.Service;

import api.DTO.CreacionCartaDTO;
import api.DTO.CreacionSolicitudCartaDTO;
import api.DTO.SolicitudCartaDTO;
import api.Entity.*;
import api.Exception.*;
import api.Repository.SolicitudCartaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitudCartaService {

    private final SolicitudCartaRepository solicitudCartaRepository;
    private final CartaService cartaService;
    private final JuegoService juegoService;

    public SolicitudCartaService(SolicitudCartaRepository solicitudCartaRepository, CartaService cartaService, JuegoService juegoService) {
        this.solicitudCartaRepository = solicitudCartaRepository;
        this.cartaService = cartaService;
        this.juegoService = juegoService;
    }

    public void guardarSolicitud(CreacionSolicitudCartaDTO creacionSol, Persona persona) {
        if(!juegoService.existeJuegoPorIdJuego(creacionSol.idJuego())){
            throw new JuegoNoEncontradoException(creacionSol.idJuego());
        }
        if(creacionSol.nombreCarta().length() > 100){
            throw new ExcesoDeCaracteresException("nombre Carta", 100);
        }
        if(cartaService.existeCartaNombreYJuego(creacionSol.nombreCarta(),creacionSol.idJuego())){
            throw new CartaYaExistenteException(creacionSol.nombreCarta());
        }

        SolicitudCreacionCarta solicitud = new SolicitudCreacionCarta();
        solicitud.setNombreCarta( creacionSol.nombreCarta());
        solicitud.setIdJuego( creacionSol.idJuego());
        solicitud.setIdSolicitante(persona.getId());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setEstado(EstadoSolicitud.EN_ESPERA);
        solicitudCartaRepository.guardarSolicitud(solicitud);
    }

    public SolicitudCartaDTO obtenerSolicitud(String id, Persona persona) {
        SolicitudCreacionCarta solicitud = solicitudCartaRepository.obtenerSolicitud(id);
        if(persona.getRol().equals(RolesPersona.ADMIN) || persona.getId().equals(solicitud.getIdSolicitante())) {
            return toSolicitudCartaDTO(solicitud);
        }
        else{
            throw new PersonaSinPermisosException(persona.getNombre());
        }
    }

    public List<SolicitudCartaDTO> obtenerTodasSolicitudesEnEspera(){
        return solicitudCartaRepository.obtenerTodasSolicitudesEnEspera().stream()
                .map(this::toSolicitudCartaDTO).toList();
    }

    public List<SolicitudCartaDTO> obtenerTodasSolicitudes(){
        return solicitudCartaRepository.obtenerTodasSolicitudes().stream()
                .map(this::toSolicitudCartaDTO).toList();
    }

    public void aceptarSolicitud(String id) {
        SolicitudCreacionCarta solicitud = solicitudCartaRepository.obtenerSolicitud(id);
        if(solicitud.getEstado().equals(EstadoSolicitud.EN_ESPERA)) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);
            solicitud.setFechaCierre(LocalDate.now());

            CreacionCartaDTO creacion = new CreacionCartaDTO(solicitud.getNombreCarta(), solicitud.getIdJuego());
            cartaService.guardarCarta(creacion);

            solicitudCartaRepository.aceptarSolicitud(solicitud);
        }
        else{
            throw new CambioEstadoSolicitudException();
        }

    }

    public void rechazarSolicitud(String id) {
        solicitudCartaRepository.rechazarSolicitud(id);
    }

    public List<SolicitudCartaDTO> obtenerSolicitudesBySolicitante(Persona persona) {

        return solicitudCartaRepository.obtenerSolicitudesBySolicitante(persona.getId()).stream()
                .map(this::toSolicitudCartaDTO).toList();



    }

    /**
     * Convierte una solicitud de Creacion de Carta en el DTO que se mostrara a los usuarios
     * @param solicitud que se debera convertir
     * @return un SolicitudCartaDTO
     */
    public SolicitudCartaDTO toSolicitudCartaDTO(SolicitudCreacionCarta solicitud) {
        Juego juego = juegoService.getJuego(solicitud.getIdJuego());
        return new SolicitudCartaDTO(
                solicitud.getId(),
                solicitud.getNombreCarta(),
                juego.getNombre(),
                solicitud.getIdJuego(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud(),
                solicitud.getFechaCierre()
        );

    }
}

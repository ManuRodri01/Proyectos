package api.Service;

import api.DTO.CreacionJuegoDTO;
import api.DTO.CreacionSolicitudJuegoDTO;
import api.DTO.SolicitudJuegoDTO;
import api.Entity.EstadoSolicitud;
import api.Entity.Persona;
import api.Entity.RolesPersona;
import api.Entity.SolicitudCreacionJuego;
import api.Exception.CambioEstadoSolicitudException;
import api.Exception.ExcesoDeCaracteresException;
import api.Exception.JuegoYaExistenteException;
import api.Exception.PersonaSinPermisosException;
import api.Repository.SolicitudJuegoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitudJuegoService {
    private final SolicitudJuegoRepository solicitudJuegoRepository;
    private final JuegoService juegoService;

    public SolicitudJuegoService(SolicitudJuegoRepository solicitudJuegoRepository, JuegoService juegoService) {
        this.solicitudJuegoRepository = solicitudJuegoRepository;
        this.juegoService = juegoService;
    }

    public void guardarSolicitud(CreacionSolicitudJuegoDTO creacionSol, Persona persona) {
        if(creacionSol.nombreJuego().length() > 50){
            throw new ExcesoDeCaracteresException("nombre Juego", 50);
        }
        if(juegoService.existeJuegoPorNombre(creacionSol.nombreJuego())){
            throw new JuegoYaExistenteException(creacionSol.nombreJuego());
        }
        SolicitudCreacionJuego solicitud = new SolicitudCreacionJuego();
        solicitud.setNombreJuego(creacionSol.nombreJuego());
        solicitud.setIdSolicitante(persona.getId());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setEstado(EstadoSolicitud.EN_ESPERA);
        solicitudJuegoRepository.guardarSolicitud(solicitud);
    }

    public SolicitudJuegoDTO obtenerSolicitud(String id, Persona persona) {
        SolicitudCreacionJuego solicitud = solicitudJuegoRepository.obtenerSolicitud(id);
        if(persona.getRol().equals(RolesPersona.ADMIN) || persona.getId().equals(solicitud.getIdSolicitante())) {
            return toSolicitudJuegoDTO(solicitud);
        }
        else{
            throw new PersonaSinPermisosException(persona.getNombre());
        }
    }

    public List<SolicitudJuegoDTO> obtenerTodasSolicitudesEnEspera(){
        return solicitudJuegoRepository.obtenerTodasSolicitudesEnEspera().stream()
                .map(this::toSolicitudJuegoDTO).toList();
    }

    public List<SolicitudJuegoDTO> obtenerTodasSolicitudes(){
        return solicitudJuegoRepository.obtenerTodasSolicitudes().stream()
                .map(this::toSolicitudJuegoDTO).toList();
    }

    public void aceptarSolicitud(String id) {
        SolicitudCreacionJuego solicitud = solicitudJuegoRepository.obtenerSolicitud(id);
        if(solicitud.getEstado().equals(EstadoSolicitud.EN_ESPERA)) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);
            solicitud.setFechaCierre(LocalDate.now());

            CreacionJuegoDTO creacionJuegoDTO = new CreacionJuegoDTO(solicitud.getNombreJuego());
            juegoService.guardarJuego(creacionJuegoDTO);

            solicitudJuegoRepository.aceptarSolicitud(solicitud);
        }
        else{
            throw new CambioEstadoSolicitudException();
        }


    }

    public void rechazarSolicitud(String id) {
        solicitudJuegoRepository.rechazarSolicitud(id);
    }

    public List<SolicitudJuegoDTO> obtenerSolicitudesBySolicitante(Persona persona) {
        return solicitudJuegoRepository.obtenerSolicitudesBySolicitante(persona.getId()).stream()
                    .map(this::toSolicitudJuegoDTO).toList();


    }

    /**
     * Convierte una solicitud de Creacion de Publicacion en el DTO que se mostrara a los usuarios
     * @param solicitud que se debera convertir
     * @return un SolicitudJuegoDTO
     */
    public SolicitudJuegoDTO toSolicitudJuegoDTO(SolicitudCreacionJuego solicitud) {
        return new SolicitudJuegoDTO(
                solicitud.getId(),
                solicitud.getNombreJuego(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud(),
                solicitud.getFechaCierre()
        );

    }
}

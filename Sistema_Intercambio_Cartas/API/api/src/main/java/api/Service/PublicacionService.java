package api.Service;



import api.DTO.*;
import api.Entity.Carta;
import api.Entity.EstadoPublicacion;
import api.Entity.Persona;
import api.Entity.Publicacion;
import api.Exception.ExcesoDeCaracteresException;
import api.Exception.PublicacionNoEncontradaException;
import api.Repository.PublicacionRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final CartaService cartaService;
    private final PublicacionesMapper publicacionesMapper;

    public PublicacionService(PublicacionRepository publicacionRepository, CartaService cartaService, PublicacionesMapper publicacionesMapper) {
        this.publicacionRepository = publicacionRepository;
        this.cartaService = cartaService;
        this.publicacionesMapper = publicacionesMapper;
    }


    public void existePublicacion(String id){
        if (!publicacionRepository.existePublicacion(id)){
            throw new PublicacionNoEncontradaException(id);
        }
    }
    public RespuetaPublicacionDTO getPublicacion (String id, Persona persona) {
        return publicacionesMapper.toRespuetaPublicacionDTO(publicacionRepository.getPublicacion(id), persona);
    }

    public Publicacion getPublicacion (String id) {
        return publicacionRepository.getPublicacion(id);
    }

    public List<Publicacion> getPublicaciones (FiltroPublicacionesDTO filtro) {
        return publicacionRepository.getPublicaciones(filtro);
    }

    public PaginacionPublicacionDTO getPublicaciones (FiltroPublicacionesDTO filtro, int pagina) {

        PseudoPaginacionPublicacionDTO pseudoPaginacion =  publicacionRepository.getPublicaciones(filtro, pagina);
        return new PaginacionPublicacionDTO(pseudoPaginacion.publicaciones().stream().map(publicacionesMapper::toPublicacionReducidaDTO).toList(),pseudoPaginacion.pagAnterior(),pseudoPaginacion.pagSiguiente());
    }

    public List<PublicacionesDePersonaDTO> getPublicacionesByPersona (String idPersona) {
        return publicacionRepository.getPublicacionesByPersona(idPersona).stream().
                map(publicacionesMapper::toPublicacionesDePersonaDTO).
                collect(Collectors.toList());
    }

    public void borrarPublicacion (String id, Persona persona) {
        publicacionRepository.borrarPublicacion(id, persona);
    }

    public CreacionPublicacionDTO guardarPublicacion(CreacionPublicacionDTO publicacion, Persona persona) {
        if(publicacion.titulo().length() > 50){
            throw new ExcesoDeCaracteresException("Titulo", 50);
        }
        if(publicacion.descripcion().length() > 500){
            throw new ExcesoDeCaracteresException("Descripcion", 500);
        }

        Carta cartaPublicada = cartaService.getCartaNoDTO((publicacion.cartaId()));

        Publicacion publicacionNueva = new Publicacion(
                publicacion.titulo(),
                persona.getId(),
                cartaPublicada.getId(),
                publicacion.descripcion(),
                publicacion.intereses(),
                publicacion.imagenes(),
                publicacion.precio(),
                publicacion.estadoConservacion(),
                LocalDateTime.now(),
                null);
        publicacionNueva.setJuegoId(cartaPublicada.getId());
        publicacionNueva.setEstadoPublicacion(EstadoPublicacion.ABIERTA);
        publicacionRepository.guardarPublicacion(publicacionNueva);
        return publicacion;
    }

    public Publicacion cerrarPublicacion(String id){
        return publicacionRepository.cerrarPublicacion(id);
    }

    public boolean estaCerrada(Publicacion publicacion) {
        return publicacion.getEstadoPublicacion().equals(EstadoPublicacion.FINALIZADA);
    }
  
    public List<Publicacion> getPublicacionesCreadasByFecha(LocalDate fecha){
        return publicacionRepository.getPublicacionesCreadasByFecha(fecha);
    }
}
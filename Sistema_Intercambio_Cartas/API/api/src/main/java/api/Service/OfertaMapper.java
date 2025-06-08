package api.Service;


import api.DTO.OfertaPersonaDTO;
import api.DTO.RespuestaOfertaDTO;
import api.Entity.Carta;
import api.Entity.Oferta;
import api.Entity.Persona;
import api.Entity.Publicacion;
import api.Repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfertaMapper {

    private final PersonaRepository personaRepository;
    private final CartaService cartaService;
    private final PublicacionService publicacionService;

    public OfertaMapper(PersonaRepository personaRepository, CartaService cartaService, PublicacionService publicacionService) {
        this.personaRepository = personaRepository;
        this.cartaService = cartaService;
        this.publicacionService = publicacionService;
    }


    /**
     * Convierte una oferta en el DTO utilizado para mostrarle las ofertas
     * al usuario en su perfil
     * @param oferta que se debera convertir
     * @return un OfertaPersonaDTO que contiene solo una fracción de la información
     */
    public OfertaPersonaDTO toOfertaPersonaDTO(Oferta oferta) {
        String imagen;
        Publicacion publicacion = publicacionService.getPublicacion(oferta.getPublicacionId());
        if( publicacion == null|| publicacion.getImagenes() == null || publicacion.getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen =publicacion.getImagenes().get(0);
        }
        String refAOferta = "/ofertas/" + oferta.getId();
        Carta cartaPublicacion = cartaService.getCartaNoDTO(publicacion.getCartaId());
        Persona ofertante = personaRepository.getPersonaById(oferta.getOfertanteId());
        String nombreOfertante = "";
        if(ofertante != null && ofertante.getNombre() != null){
            nombreOfertante = ofertante.getNombre();
        }
        return new OfertaPersonaDTO(
                cartaPublicacion.getNombre(),
                oferta.getEstadoOferta(),
                publicacion.getTitulo(),
                nombreOfertante,
                oferta.getFecha(),
                imagen,
                refAOferta,
                oferta.getId());

    }

    /**
     * Convierte una oferta en el DTO utilizado para ver el detalle de las ofertas
     * @param oferta que se debera convertir
     * @return un RespuestaOfertaDTO
     */
    public RespuestaOfertaDTO toRespuestaOfertaDTO(Oferta oferta, Persona persona, Publicacion publicacion) {
        String imagen;
        if(publicacion.getImagenes() == null || publicacion.getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen =publicacion.getImagenes().get(0);
        }
        boolean esPublicador = publicacion.getPublicadorId().equals(persona.getId());
        Persona ofertante = personaRepository.getPersonaById(oferta.getOfertanteId());
        String nombreOfertante = "";
        if(ofertante != null && ofertante.getNombre() != null){
            nombreOfertante = ofertante.getNombre();
        }
        Persona publicador = personaRepository.getPersonaById(oferta.getPublicadorId());
        String nombrePublicador = "";
        if(publicador != null && publicador.getNombre() != null){
            nombrePublicador = publicador.getNombre();
        }

        List<String> cartasNombre = new ArrayList<>();
        for(String cartaId : oferta.getCartas()){
            Carta carta = cartaService.getCartaNoDTO(cartaId);
            cartasNombre.add(carta.getNombre());
        }

        return new RespuestaOfertaDTO(
                publicacion.getTitulo(),
                nombreOfertante,
                nombrePublicador,
                oferta.getId(),
                oferta.getEstadoOferta(),
                oferta.getFecha(),
                oferta.getValor(),
                cartasNombre,
                imagen,
                esPublicador

        );
    }
}

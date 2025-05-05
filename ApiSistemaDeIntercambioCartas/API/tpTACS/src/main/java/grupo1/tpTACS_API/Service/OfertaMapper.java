package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.OfertaPersonaDTO;
import grupo1.tpTACS_API.DTO.RespuestaOfertaDTO;
import grupo1.tpTACS_API.Entity.Carta;
import grupo1.tpTACS_API.Entity.Oferta;
import grupo1.tpTACS_API.Entity.Persona;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OfertaMapper {
    /**
     * Convierte una oferta en el DTO utilizado para mostrarle las ofertas
     * al usuario en su perfil
     * @param oferta que se debera convertir
     * @return un OfertaPersonaDTO que contiene solo una fracción de la información
     */
    public OfertaPersonaDTO toOfertaPersonaDTO(Oferta oferta) {
        String imagen;
        if( oferta.getPublicacion().getImagenes() == null || oferta.getPublicacion().getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen =oferta.getPublicacion().getImagenes().get(0);
        }
        String refAOferta = "/ofertas/" + oferta.getId();
        return new OfertaPersonaDTO(
                oferta.getPublicacion().getCarta().getNombre(),
                oferta.getEstadoOferta(),
                oferta.getPublicacion().getTitulo(),
                oferta.getOfertante().getNombre(),
                oferta.getFecha(),
                imagen,
                refAOferta,
                oferta.getId());

    }

    public RespuestaOfertaDTO toRespuestaOfertaDTO(Oferta oferta, Persona persona) {
        String imagen;
        if(oferta.getPublicacion().getImagenes() == null || oferta.getPublicacion().getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen =oferta.getPublicacion().getImagenes().get(0);
        }
        boolean esPublicador = oferta.getPublicacion().getPublicador().getNombre().equals(persona.getNombre());
        return new RespuestaOfertaDTO(
                oferta.getPublicacion().getTitulo(),
                oferta.getOfertante().getNombre(),
                oferta.getPublicacion().getPublicador().getNombre(),
                oferta.getId(),
                oferta.getEstadoOferta(),
                oferta.getFecha(),
                oferta.getValor(),
                oferta.getCartas().stream().map(Carta::getNombre).collect(Collectors.toList()),
                imagen,
                esPublicador

        );
    }
}

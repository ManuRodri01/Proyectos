package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.Entity.Oferta;
import manuRodri.intercambioCartas.DTO.OfertaPersonaDTO;
import org.springframework.stereotype.Service;

@Service
public class OferPersonaMapper {
    /**
     * Convierte una oferta en el DTO utilizado para mostrarle las ofertas
     * al usuario en su perfil
     * @param oferta que se debera convertir
     * @return un OfertaPersonaDTO que contiene solo una fracción de la información
     */
    public OfertaPersonaDTO toOfertaPersonaDTO(Oferta oferta) {
        String imagen;
        if(oferta.getPublicacion().getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen =oferta.getPublicacion().getImagenes().get(0);
        }
        String refAOferta = "/ofertas/" + oferta.getId();
        return new OfertaPersonaDTO(
                oferta.getPublicacion().getCarta().getNombre(),
                oferta.getEstadoOferta(),
                oferta.getOfertante().getNombre(),
                oferta.getFecha(),
                imagen,
                refAOferta);

    }
}

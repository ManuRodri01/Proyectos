package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.Entity.Publicacion;
import manuRodri.intercambioCartas.DTO.PublicacionesDePersonaDTO;
import org.springframework.stereotype.Service;

@Service
public class PubliPersonasMapper {
    /**
     * Convierte una publicación en el DTO utilizado para mostrarle sus publicaciones
     * al usuario en su perfil
     * @param publicacion que se debera convertir
     * @return un PublicacionDePersonaDTO que contiene solo una fracción de la información
     */
    public PublicacionesDePersonaDTO toPublicacionesDePersonaDTO(Publicacion publicacion) {
        String imagen;
        if(publicacion.getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen = publicacion.getImagenes().get(0);
        }
        String refAPublicacion = "/publicaciones/" + publicacion.getId();
        return new PublicacionesDePersonaDTO(
                imagen,
                publicacion.getEstadoConservacion(),
                publicacion.getCarta().getNombre(),
                publicacion.getFechaPublicacion(),
                refAPublicacion);

    }
}

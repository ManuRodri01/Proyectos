package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.PublicacionReducidaDTO;
import grupo1.tpTACS_API.DTO.PublicacionesDePersonaDTO;
import grupo1.tpTACS_API.DTO.RespuetaPublicacionDTO;
import grupo1.tpTACS_API.Entity.Carta;
import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Entity.Publicacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicacionesMapper {
    /**
     * Convierte una publicación en el DTO utilizado para mostrarle sus publicaciones
     * al usuario en su perfil
     * @param publicacion que se debera convertir
     * @return un PublicacionDePersonaDTO que contiene solo una fracción de la información
     */
    public PublicacionesDePersonaDTO toPublicacionesDePersonaDTO(Publicacion publicacion) {
        String imagen;
        if(publicacion.getImagenes() == null || publicacion.getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen = publicacion.getImagenes().get(0);
        }
        String refAPublicacion = "/publicaciones/" + publicacion.getId();
        return new PublicacionesDePersonaDTO(
                publicacion.getTitulo(),
                imagen,
                publicacion.getEstadoConservacion(),
                publicacion.getCarta().getNombre(),
                publicacion.getFechaPublicacion(),
                publicacion.getFechaCierre(),
                refAPublicacion,
                publicacion.getId());

    }

    public RespuetaPublicacionDTO toRespuetaPublicacionDTO(Publicacion publicacion, Persona persona) {
        List<String> cartas = new ArrayList<>();
        if(publicacion.getIntereses() != null){
            for(Carta carta : publicacion.getIntereses()) {
                cartas.add(carta.getNombre());
            }
        }
        String nombrePublicador;
        String nombreCarta;
        String nombreJuego;
        if(publicacion.getPublicador() == null || publicacion.getPublicador().getNombre() == null){
            nombrePublicador = "";
        }
        else{
            nombrePublicador = publicacion.getPublicador().getNombre();
        }
        if(publicacion.getCarta() == null || publicacion.getCarta().getNombre() == null){
            nombreCarta = "";
        }
        else{
            nombreCarta = publicacion.getCarta().getNombre();
        }
        if(publicacion.getCarta() == null || publicacion.getCarta().getJuego() == null || publicacion.getCarta().getJuego().getNombre() == null){
            nombreJuego = "";
        }
        else{
            nombreJuego = publicacion.getCarta().getJuego().getNombre();
        }



        boolean esPublicador = nombrePublicador.equals(persona.getNombre());
        return new RespuetaPublicacionDTO(
                publicacion.getTitulo(),
                nombreCarta,
                nombreJuego,
                publicacion.getDescripcion(),
                cartas,
                publicacion.getImagenes(),
                publicacion.getPrecio(),
                publicacion.getEstadoConservacion(),
                publicacion.getFechaPublicacion(),
                nombrePublicador,
                esPublicador,
                publicacion.getId()
        );
    }

    public PublicacionReducidaDTO toPublicacionReducidaDTO(Publicacion publicacion) {
        String nombreCarta;
        if(publicacion.getCarta() == null || publicacion.getCarta().getNombre() == null){
            nombreCarta = "";
        }
        else{
            nombreCarta = publicacion.getCarta().getNombre();
        }
        String imagen;
        if(publicacion.getImagenes() == null || publicacion.getImagenes().isEmpty()){
            imagen = "";
        }
        else{
            imagen = publicacion.getImagenes().get(0);
        }
        String refAPublicacion = "/publicaciones/" + publicacion.getId();

        return new PublicacionReducidaDTO(
                publicacion.getTitulo(),
                imagen,
                publicacion.getEstadoConservacion(),
                nombreCarta,
                refAPublicacion,
                publicacion.getId()
        );
    }
}

package api.Service;


import api.DTO.PublicacionReducidaDTO;
import api.DTO.PublicacionesDePersonaDTO;
import api.DTO.RespuetaPublicacionDTO;
import api.Entity.Carta;
import api.Entity.Juego;
import api.Entity.Persona;
import api.Entity.Publicacion;
import api.Repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicacionesMapper {

    private final CartaService cartaService;
    private final PersonaRepository personaRepository;
    private final JuegoService juegoService;

    public PublicacionesMapper(CartaService cartaService, PersonaRepository personaRepository, JuegoService juegoService) {
        this.cartaService = cartaService;
        this.personaRepository = personaRepository;
        this.juegoService = juegoService;
    }

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
        Carta carta = cartaService.getCartaNoDTO(publicacion.getCartaId());
        return new PublicacionesDePersonaDTO(
                publicacion.getTitulo(),
                imagen,
                publicacion.getEstadoConservacion(),
                carta.getNombre(),
                publicacion.getFechaPublicacion(),
                publicacion.getFechaCierre(),
                refAPublicacion,
                publicacion.getId());

    }

    /**
     * Convierte una publicación en el DTO utilizado para mostrar los detalles de la publicacion
     * @param publicacion que se debera convertir
     * @return un RespuestaPublicacionDTO
     */
    public RespuetaPublicacionDTO toRespuetaPublicacionDTO(Publicacion publicacion, Persona persona) {
        List<String> cartas = new ArrayList<>();
        if(publicacion.getIntereses() != null){
            for(String cartaId : publicacion.getIntereses()) {
                Carta carta = cartaService.getCartaNoDTO(cartaId);
                cartas.add(carta.getNombre());
            }
        }
        String nombrePublicador = "";
        String nombreCarta = "";
        String nombreJuego;
        Persona publicador = personaRepository.getPersonaById(publicacion.getPublicadorId());
        Carta carta = cartaService.getCartaNoDTO(publicacion.getCartaId());
        if(publicador != null && publicador.getNombre() != null){
            nombrePublicador = publicador.getNombre();
        }
        if(carta != null && carta.getNombre() != null){
            nombreCarta = carta.getNombre();
        }

        if(carta == null || carta.getJuegoId() == null){
            nombreJuego = "";
        }
        else{
            Juego juego = juegoService.getJuego(carta.getJuegoId());
            if(juego != null && juego.getNombre() != null){
                nombreJuego = juego.getNombre();
            }
            else{
                nombreJuego = "";
            }
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
                publicacion.getId(),
                publicacion.getFechaCierre()
        );
    }

    /**
     * Convierte una publicación en el DTO utilizado para mostrar en la lista de publicaciones
     * @param publicacion que se debera convertir
     * @return un PublicacionReducidaDTO que contiene solo una fracción de la información
     */
    public PublicacionReducidaDTO toPublicacionReducidaDTO(Publicacion publicacion) {
        String nombreCarta = "";
        Carta carta = cartaService.getCartaNoDTO(publicacion.getCartaId());
        if(carta != null && carta.getNombre() != null){
            nombreCarta = carta.getNombre();
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

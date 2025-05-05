package grupo1.tpTACS_API.Service;



import grupo1.tpTACS_API.DTO.CreacionPublicacionDTO;
import grupo1.tpTACS_API.DTO.FiltroPublicacionesDTO;
import grupo1.tpTACS_API.DTO.PaginacionPublicacionDTO;
import grupo1.tpTACS_API.DTO.RespuetaPublicacionDTO;
import grupo1.tpTACS_API.Entity.Carta;
import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Entity.Publicacion;
import grupo1.tpTACS_API.Exception.PublicacionNoEncontradaException;
import grupo1.tpTACS_API.Repository.CartaRepository;
import grupo1.tpTACS_API.Repository.PublicacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final CartaRepository cartaRepository;
    private final PublicacionesMapper publicacionesMapper;

    public PublicacionService(PublicacionRepository publicacionRepository, CartaRepository cartaRepository, PublicacionesMapper publicacionesMapper) {
        this.publicacionRepository = publicacionRepository;
        this.cartaRepository = cartaRepository;
        this.publicacionesMapper = publicacionesMapper;
    }

    public void existePublicacion(int id){
        if (!publicacionRepository.existePublicacion(id)){
            throw new PublicacionNoEncontradaException(id);
        }
    }
    public RespuetaPublicacionDTO getPublicacion (int id, Persona persona) {
        existePublicacion(id);
        return publicacionesMapper.toRespuetaPublicacionDTO(publicacionRepository.getPublicacion(id), persona);
    }

    public List<Publicacion> getPublicaciones (FiltroPublicacionesDTO filtro) {
        return publicacionRepository.getPublicaciones(filtro);
    }

    public PaginacionPublicacionDTO getPublicaciones (FiltroPublicacionesDTO filtro, int pagina) {

        return publicacionRepository.getPublicaciones(filtro, pagina);
    }

    public void borrarPublicacion (int id, Persona persona) {
        publicacionRepository.borrarPublicacion(id, persona);
    }

    public CreacionPublicacionDTO guardarPublicacion(CreacionPublicacionDTO publicacion, Persona persona) {
        Carta cartaPublicada = cartaRepository.obtenerCarta((publicacion.cartaId()));
        List<Carta> intereses = publicacion.intereses().stream().map(cartaRepository::obtenerCarta).collect(Collectors.toList());

        Publicacion publicacionNueva = new Publicacion(
                publicacion.titulo(),
                persona,
                cartaPublicada,
                publicacion.descripcion(),
                intereses,
                publicacion.imagenes(),
                publicacion.precio(),
                publicacion.estadoConservacion(),
                LocalDateTime.now(),
                null);
        publicacionRepository.guardarPublicacion(publicacionNueva);
        return publicacion;
    }

    public Publicacion cerrarPublicacion(int id){
        return publicacionRepository.cerrarPublicacion(id);
    }

}
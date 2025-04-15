package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.CreacionPublicacionDTO;
import manuRodri.intercambioCartas.DTO.FiltroPublicacionesDTO;
import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Entity.Persona;
import manuRodri.intercambioCartas.Entity.Publicacion;
import manuRodri.intercambioCartas.Exception.PublicacionNoEncontradaException;
import manuRodri.intercambioCartas.Repository.CartaRepository;
import manuRodri.intercambioCartas.Repository.PublicacionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final CartaRepository cartaRepository;

    public PublicacionService(PublicacionRepository publicacionRepository, CartaRepository cartaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.cartaRepository = cartaRepository;
    }

    public void existePublicacion(int id){
        if (!publicacionRepository.existePublicacion(id)){
            throw new PublicacionNoEncontradaException(id);
        }
    }
    public Publicacion getPublicacion (int id) {
        existePublicacion(id);
        return publicacionRepository.getPublicacion(id);
    }

    public List<Publicacion> getPublicaciones (FiltroPublicacionesDTO filtro) {
        return publicacionRepository.getPublicaciones(filtro);
    }

    public CreacionPublicacionDTO guardarPublicacion(CreacionPublicacionDTO publicacion, Persona persona) {
        Carta cartaPublicada = cartaRepository.obtenerCarta((publicacion.cartaId()));
        List<Carta> intereses = publicacion.intereses().stream().map(cartaRepository::obtenerCarta).collect(Collectors.toList());

        Publicacion publicacionNueva = new Publicacion(
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
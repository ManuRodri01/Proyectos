package grupo1.tpTACS_API.Service;



import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import grupo1.tpTACS_API.DTO.CreacionOfertaDTO;
import grupo1.tpTACS_API.DTO.RespuestaOfertaDTO;
import grupo1.tpTACS_API.Entity.*;
import grupo1.tpTACS_API.Exception.OfertaNoEncontradaException;
import grupo1.tpTACS_API.Exception.PersonaSinPermisosException;
import grupo1.tpTACS_API.Repository.CartaRepository;
import grupo1.tpTACS_API.Repository.OfertaRepository;
import grupo1.tpTACS_API.Repository.PublicacionRepository;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

	private final OfertaRepository ofertaRepository;
	private final CartaRepository cartaRepository;
	private final PublicacionRepository publicacionRepository;
	private final OfertaMapper ofertaMapper;


	public OfertaService(OfertaRepository ofertaRepository, CartaRepository cartaRepository, PublicacionRepository publicacionRepository, OfertaMapper ofertaMapper){
		this.ofertaRepository = ofertaRepository;
        this.cartaRepository = cartaRepository;
        this.publicacionRepository = publicacionRepository;
        this.ofertaMapper = ofertaMapper;
    }

	public CreacionOfertaDTO guardarOferta(CreacionOfertaDTO oferta, Persona ofertante){
		Publicacion publicacion = publicacionRepository.getPublicacion(oferta.publicacionId());
		List<Carta> cartas;
		if(oferta.cartas() != null){
			cartas = oferta.cartas().stream().map(cartaRepository::obtenerCarta).collect(Collectors.toList());
		}
		else{
			cartas = new ArrayList<>();
		}

		Oferta nuevaOferta = new Oferta();
		nuevaOferta.setCartas(cartas);
		nuevaOferta.setPublicacion(publicacion);
		if(oferta.valor() != null){
			nuevaOferta.setValor(oferta.valor());
		}
		else{
			nuevaOferta.setValor(-1f);
		}
		nuevaOferta.setFecha(LocalDate.now());
		nuevaOferta.setEstadoOferta(EstadoOferta.EN_ESPERA);
		nuevaOferta.setOfertante(ofertante);

		ofertaRepository.guardarOferta(nuevaOferta);

		return oferta;
	}

	public void existeOferta(int id){
		if (!ofertaRepository.existeOferta(id)){
			throw new OfertaNoEncontradaException(id);
		}

	}

	public RespuestaOfertaDTO getOferta(int id, Persona persona){

		Oferta oferta = getOferta(id);
		if(!(esElOfertante(oferta, persona) || esElPublicador(oferta.getPublicacion(), persona))){
			throw new PersonaSinPermisosException(persona.getNombre());
		}

		return ofertaMapper.toRespuestaOfertaDTO(oferta, persona);

	}

	private Oferta getOferta(int id){
		existeOferta(id);
		return ofertaRepository.getOferta(id);
	}

	public Oferta borrarOferta(int id, Persona persona){
		existeOferta(id);
		if(!esElOfertante(getOferta(id), persona)){
			throw new PersonaSinPermisosException(persona.getNombre());
		}
		return ofertaRepository.borrarOferta(id);
	}

	public Oferta aceptarOferta (int idOferta, Persona persona) {
		Oferta oferta = getOferta(idOferta);
		if(!esElPublicador(oferta.getPublicacion(), persona)){
			 throw new PersonaSinPermisosException(persona.getNombre());
		}
		oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
		publicacionRepository.cerrarPublicacion(oferta.getPublicacion().getId());
		rechazarOfertasDePublicacion(oferta.getPublicacion().getId());
		return oferta;

	}

	private boolean esElOfertante(Oferta oferta, Persona persona) {
		return oferta.getOfertante().getNombre().equals(persona.getNombre());
	}

	private boolean esElPublicador(Publicacion publicacion, Persona persona) {
		return publicacion.getPublicador().getUsername().equals(persona.getUsername());
	}

	private void rechazarOferta (int idOferta) {
		Oferta oferta = getOferta(idOferta);
		if(!oferta.getEstadoOferta().equals(EstadoOferta.ACEPTADA)){
			oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
		}

	}

	public Oferta rechazarOferta(int idOferta,Persona persona) {
		Oferta oferta = getOferta(idOferta);
		if(!esElPublicador(oferta.getPublicacion(), persona)){
			throw new PersonaSinPermisosException(persona.getNombre());
		}
		oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
		return oferta;
	}

	public void rechazarOfertasDePublicacion (int idPublicacion) {
		List<Oferta> ofertas = ofertaRepository.getOfertasAPublicacion(idPublicacion);
		ofertas.forEach(oferta -> {rechazarOferta(oferta.getId());});
	}

}

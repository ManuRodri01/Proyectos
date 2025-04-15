package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.CreacionOfertaDTO;
import manuRodri.intercambioCartas.Entity.*;
import manuRodri.intercambioCartas.Exception.OfertaNoEncontradaException;
import manuRodri.intercambioCartas.Exception.PersonaSinPermisosException;
import manuRodri.intercambioCartas.Repository.CartaRepository;
import manuRodri.intercambioCartas.Repository.OfertaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import manuRodri.intercambioCartas.Repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

	@Autowired
	private OfertaRepository ofertaRepository;
	@Autowired
	private CartaRepository cartaRepository;
	@Autowired
	private PublicacionRepository publicacionRepository;


	public OfertaService(OfertaRepository ofertaRepository){
		this.ofertaRepository = ofertaRepository;
	}

	public CreacionOfertaDTO guardarOferta(CreacionOfertaDTO oferta, Persona ofertante){
		Publicacion publicacion = publicacionRepository.getPublicacion(oferta.publicacionId());
		List<Carta> cartas = oferta.cartas().stream().map(cartaRepository::obtenerCarta).collect(Collectors.toList());

		Oferta nuevaOferta = new Oferta();
		nuevaOferta.setCartas(cartas);
		nuevaOferta.setPublicacion(publicacion);
		nuevaOferta.setValor(oferta.valor());
		nuevaOferta.setFecha(LocalDate.now());
		nuevaOferta.setEstadoOferta(estadoOferta.EN_ESPERA);
		nuevaOferta.setOfertante(ofertante);

		ofertaRepository.guardarOferta(nuevaOferta);

		return oferta;
	}

	public void existeOferta(int id){
		if (!ofertaRepository.existeOferta(id)){
			throw new OfertaNoEncontradaException(id);
		}
	}

	public Oferta getOferta(int id, Persona persona){

		Oferta oferta = getOferta(id);
		if(!(esElOfertante(oferta, persona) || esElPublicador(oferta.getPublicacion(), persona))){
			throw new PersonaSinPermisosException(persona.getNombre());
		}

		return oferta;

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
		oferta.setEstadoOferta(estadoOferta.ACEPTADA);
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
		if(!oferta.getEstadoOferta().equals(estadoOferta.ACEPTADA)){
			oferta.setEstadoOferta(estadoOferta.RECHAZADA);
		}

	}

	public Oferta rechazarOferta(int idOferta,Persona persona) {
		Oferta oferta = getOferta(idOferta);
		if(!esElPublicador(oferta.getPublicacion(), persona)){
			throw new PersonaSinPermisosException(persona.getNombre());
		}
		oferta.setEstadoOferta(estadoOferta.RECHAZADA);
		return oferta;
	}

	public void rechazarOfertasDePublicacion (int idPublicacion) {
		List<Oferta> ofertas = ofertaRepository.getOfertasAPublicacion(idPublicacion);
		ofertas.forEach(oferta -> {rechazarOferta(oferta.getId());});
	}

}

package api.Service;



import api.DTO.EdicionOfertaDTO;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import api.DTO.CreacionOfertaDTO;
import api.DTO.OfertaPersonaDTO;
import api.DTO.RespuestaOfertaDTO;
import api.Entity.EstadoOferta;
import api.Entity.Oferta;
import api.Entity.Persona;
import api.Entity.Publicacion;
import api.Exception.OfertaNoEncontradaException;
import api.Exception.PersonaSinPermisosException;
import api.Exception.PublicacionFinalizada;
import api.Repository.OfertaRepository;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

	private final OfertaRepository ofertaRepository;
	private final CartaService cartaService;
	private final PublicacionService publicacionService;
	private final OfertaMapper ofertaMapper;

    public OfertaService(OfertaRepository ofertaRepository, CartaService cartaService, PublicacionService publicacionService, OfertaMapper ofertaMapper) {
        this.ofertaRepository = ofertaRepository;
        this.cartaService = cartaService;
        this.publicacionService = publicacionService;
        this.ofertaMapper = ofertaMapper;
    }


	public CreacionOfertaDTO guardarOferta(CreacionOfertaDTO oferta, Persona ofertante){
		Publicacion publicacion = publicacionService.getPublicacion(oferta.publicacionId());
		if(this.publicacionService.estaCerrada(publicacion))
			throw new PublicacionFinalizada(publicacion.getId());
		List<String> cartas;
		if(oferta.cartas() != null){
			cartas = oferta.cartas();

		}
		else{
			cartas = new ArrayList<>();
		}

		Oferta nuevaOferta = new Oferta();
		nuevaOferta.setCartas(cartas);
		nuevaOferta.setPublicacionId(publicacion.getId());
		if(oferta.valor() != null){
			nuevaOferta.setValor(oferta.valor());
		}
		else{
			nuevaOferta.setValor(-1f);
		}
		nuevaOferta.setFecha(LocalDate.now());
		nuevaOferta.setEstadoOferta(EstadoOferta.EN_ESPERA);
		nuevaOferta.setOfertanteId(ofertante.getId());
		nuevaOferta.setPublicadorId(publicacion.getPublicadorId());
		nuevaOferta.setPublicacionId(publicacion.getId());

		ofertaRepository.guardarOferta(nuevaOferta);

		return oferta;
	}

	public void existeOferta(String id){
		if (!ofertaRepository.existeOferta(id)){
			throw new OfertaNoEncontradaException(id);
		}

	}

	public List<RespuestaOfertaDTO> getOfertaByPublicacion(String id, Persona persona) {
		Publicacion publicacion = publicacionService.getPublicacion(id);
		return ofertaRepository.getOfertasByPublicacion(id).stream().map(oferta -> ofertaMapper.toRespuestaOfertaDTO(oferta, persona, publicacion)).toList();
	}

	public RespuestaOfertaDTO getOferta(String id, Persona persona){

		Oferta oferta = getOferta(id);
		Publicacion publicacion = publicacionService.getPublicacion(oferta.getPublicacionId());
		if(!(esElOfertante(oferta.getOfertanteId(), persona) || esElPublicador(oferta.getPublicadorId(), persona))){
			throw new PersonaSinPermisosException(persona.getNombre());
		}

		return ofertaMapper.toRespuestaOfertaDTO(oferta, persona, publicacion);

	}

	public void guardarOferta(Oferta oferta) {
		ofertaRepository.guardarOferta(oferta);
	}

	private Oferta getOferta(String id){
		return ofertaRepository.getOferta(id);
	}

	public Oferta borrarOferta(String id, Persona persona){
		Oferta oferta = getOferta(id);
		if(!esElOfertante(oferta.getOfertanteId(), persona)){
			throw new PersonaSinPermisosException(persona.getNombre());
		}
		return ofertaRepository.borrarOferta(id);
	}

	public Oferta aceptarOferta (String idOferta, Persona persona) {
		Oferta oferta = getOferta(idOferta);
		if(!esElPublicador(oferta.getPublicadorId(), persona)){
			 throw new PersonaSinPermisosException(persona.getNombre());
		}
		Publicacion publicacion = this.publicacionService.getPublicacion(oferta.getPublicacionId());
		if(this.publicacionService.estaCerrada(publicacion))
			throw new PublicacionFinalizada(oferta.getPublicacionId());
		oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
		oferta.setFechaCierre(LocalDate.now());
		guardarOferta(oferta);
		publicacionService.cerrarPublicacion(oferta.getPublicacionId());
		rechazarOfertasDePublicacion(oferta.getPublicacionId());
		return oferta;

	}

	private boolean esElOfertante(String ofertanteId, Persona persona) {
		return ofertanteId.equals(persona.getId());
	}

	private boolean esElPublicador(String publicadorId, Persona persona) {
		return publicadorId.equals(persona.getId());
	}

	private void rechazarOferta (String idOferta) {
		Oferta oferta = getOferta(idOferta);
		if(!oferta.getEstadoOferta().equals(EstadoOferta.ACEPTADA)){
			oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
			oferta.setFechaCierre(LocalDate.now());
			guardarOferta(oferta);
		}

	}

	public Oferta rechazarOferta(String idOferta, Persona persona) {
		Oferta oferta = getOferta(idOferta);
		if(!esElPublicador(oferta.getPublicadorId(), persona)){
			throw new PersonaSinPermisosException(persona.getNombre());
		}
		Publicacion publicacion = this.publicacionService.getPublicacion(oferta.getPublicacionId());
		if(this.publicacionService.estaCerrada(publicacion))
			throw new PublicacionFinalizada(oferta.getPublicacionId());
		oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
		guardarOferta(oferta);
		return oferta;
	}

	public void rechazarOfertasDePublicacion (String idPublicacion) {
		List<Oferta> ofertas = ofertaRepository.getOfertasAPublicacion(idPublicacion);
		ofertas.forEach(oferta -> {rechazarOferta(oferta.getId());});
	}

	public List<OfertaPersonaDTO> getOfertasRecibidasByPersona (String idPersona) {
		return ofertaRepository.getOfertasRecibidasByPersona(idPersona).stream().
				map(ofertaMapper::toOfertaPersonaDTO).
				collect(Collectors.toList());
	}

	public List<OfertaPersonaDTO> getOfertasHechasByPersona (String idPersona) {
		return ofertaRepository.getOfertasHechasByPersona(idPersona).stream().
				map(ofertaMapper::toOfertaPersonaDTO).
				collect(Collectors.toList());
	}

	public Oferta editarOferta(String id, Persona persona, EdicionOfertaDTO ofertaDTO){
		if(ofertaDTO.estadoOferta() == EstadoOferta.ACEPTADA){
			return aceptarOferta(id, persona);
		}
		if(ofertaDTO.estadoOferta() == EstadoOferta.RECHAZADA){
			return rechazarOferta(id, persona);
		}
		return null;
	}

	public List<Oferta> getOfertasRechazadasByFecha(LocalDate fecha){
			return ofertaRepository.getOfertasRechazadasByFecha(fecha);
	}

	public List<Oferta> getOfertasAceptadasByFecha(LocalDate fecha){
			return ofertaRepository.getOfertasAceptadasByFecha(fecha);
	}

	public List<Oferta> getOfertasCreadasByFecha(LocalDate fecha){
			return ofertaRepository.getOfertasCreadasByFecha(fecha);
	}
}

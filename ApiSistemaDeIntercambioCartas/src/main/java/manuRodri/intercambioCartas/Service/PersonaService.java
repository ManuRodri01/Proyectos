package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.OfertaPersonaDTO;
import manuRodri.intercambioCartas.Entity.Persona;
import manuRodri.intercambioCartas.DTO.PublicacionesDePersonaDTO;
import manuRodri.intercambioCartas.Repository.OfertaRepository;
import manuRodri.intercambioCartas.Repository.PersonaRepository;
import manuRodri.intercambioCartas.Repository.PublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PublicacionRepository publicacionRepository;
    private final PubliPersonasMapper publicPersonasMapper;
    private final OferPersonaMapper oferPersonaMapper;
    private final OfertaRepository ofertaRepository;

    public PersonaService(PersonaRepository personaRepository, PublicacionRepository publicacionRepository, PubliPersonasMapper publicPersonasMapper, OferPersonaMapper oferPersonaMapper, OfertaRepository ofertaRepository) {
        this.personaRepository = personaRepository;
        this.publicacionRepository = publicacionRepository;
        this.publicPersonasMapper = publicPersonasMapper;
        this.oferPersonaMapper = oferPersonaMapper;
        this.ofertaRepository = ofertaRepository;
    }

    public Persona getPersona (String nombre) {
        return personaRepository.getPersona(nombre);
    }

    public List<PublicacionesDePersonaDTO> getPublicacionesDePersona (String nombre) {
        return publicacionRepository.getPublicacionesByPersona(nombre).stream().
                map(publicPersonasMapper::toPublicacionesDePersonaDTO).
                collect(Collectors.toList());
    }

    public List<OfertaPersonaDTO> getOfertasHechas (String nombre) {
        return ofertaRepository.getOfertasHechasByPersona(nombre).stream().
                map(oferPersonaMapper::toOfertaPersonaDTO).
                collect(Collectors.toList());
    }

    public List<OfertaPersonaDTO> getOfertasRecibidas (String nombre) {
        return ofertaRepository.getOfertasRecibidasByPersona(nombre).stream().
                map(oferPersonaMapper::toOfertaPersonaDTO).
                collect(Collectors.toList());
    }





}

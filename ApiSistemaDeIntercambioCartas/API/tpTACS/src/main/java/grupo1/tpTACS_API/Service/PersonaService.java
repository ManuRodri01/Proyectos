package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.OfertaPersonaDTO;
import grupo1.tpTACS_API.DTO.PersonaDTO;
import grupo1.tpTACS_API.DTO.PublicacionesDePersonaDTO;
import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Repository.OfertaRepository;
import grupo1.tpTACS_API.Repository.PersonaRepository;
import grupo1.tpTACS_API.Repository.PublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PublicacionRepository publicacionRepository;
    private final PublicacionesMapper publicPersonasMapper;
    private final OfertaMapper ofertaMapper;
    private final OfertaRepository ofertaRepository;

    public PersonaService(PersonaRepository personaRepository, PublicacionRepository publicacionRepository, PublicacionesMapper publicPersonasMapper, OfertaMapper ofertaMapper, OfertaRepository ofertaRepository) {
        this.personaRepository = personaRepository;
        this.publicacionRepository = publicacionRepository;
        this.publicPersonasMapper = publicPersonasMapper;
        this.ofertaMapper = ofertaMapper;
        this.ofertaRepository = ofertaRepository;
    }

    public PersonaDTO getPersona (String nombre) {
        Persona persona = personaRepository.getPersona(nombre);
        return new PersonaDTO(persona.getNombre(),persona.getRol());
    }

    public List<PublicacionesDePersonaDTO> getPublicacionesDePersona (String nombre) {
        return publicacionRepository.getPublicacionesByPersona(nombre).stream().
                map(publicPersonasMapper::toPublicacionesDePersonaDTO).
                collect(Collectors.toList());
    }

    public List<OfertaPersonaDTO> getOfertasHechas (String nombre) {
        return ofertaRepository.getOfertasHechasByPersona(nombre).stream().
                map(ofertaMapper::toOfertaPersonaDTO).
                collect(Collectors.toList());
    }

    public List<OfertaPersonaDTO> getOfertasRecibidas (String nombre) {
        return ofertaRepository.getOfertasRecibidasByPersona(nombre).stream().
                map(ofertaMapper::toOfertaPersonaDTO).
                collect(Collectors.toList());
    }





}

package api.Service;


import api.DTO.OfertaPersonaDTO;
import api.DTO.PersonaDTO;
import api.DTO.PublicacionesDePersonaDTO;
import api.Entity.Persona;
import api.Repository.PersonaRepository;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PublicacionService publicacionService;
    private final OfertaService ofertaService;

    public PersonaService(PersonaRepository personaRepository, PublicacionService publicacionService, OfertaService ofertaService) {
        this.personaRepository = personaRepository;
        this.publicacionService = publicacionService;
        this.ofertaService = ofertaService;
    }


    public PersonaDTO getPersona (String nombre) {
        Persona persona = personaRepository.getPersona(nombre);
        return new PersonaDTO(persona.getNombre(),persona.getRol());
    }

    public Persona getPersonaById(String id) {
        return personaRepository.getPersonaById(id);
    }

    public List<PublicacionesDePersonaDTO> getPublicacionesDePersona (String idPersona) {
        return publicacionService.getPublicacionesByPersona(idPersona);
    }

    public List<OfertaPersonaDTO> getOfertasHechas (String idPersona) {
        return ofertaService.getOfertasHechasByPersona(idPersona);
    }

    public List<OfertaPersonaDTO> getOfertasRecibidas (String idPersona) {
        return ofertaService.getOfertasRecibidasByPersona(idPersona);
    }


    public List<Persona> getPersonasByFechaCreacion(LocalDate fecha){
        return personaRepository.getPersonasByFechaCreacion(fecha);
    }


}

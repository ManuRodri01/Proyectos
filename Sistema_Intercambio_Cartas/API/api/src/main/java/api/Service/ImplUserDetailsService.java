package api.Service;


import api.Entity.Persona;
import api.Exception.PersonaNoEncontradaException;
import api.Repository.PersonaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ImplUserDetailsService implements UserDetailsService {
    private final PersonaRepository personaRepository;

    public ImplUserDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }


    /**
     * Obtiene un UserDetails, obtenido en base al nombre de usuario,
     * que se utiliza en la autenticación en el sistema de Seguridad.
     *
     * @param username nombre de usuario de la persona
     * @return un UserDetails que corresponde a la persona
     * @throws PersonaNoEncontradaException en caso de que el usuario no exista
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Persona persona = personaRepository.findByNombre(username).
                orElseThrow(() ->
                        new PersonaNoEncontradaException(username));


        return persona;
    }
}

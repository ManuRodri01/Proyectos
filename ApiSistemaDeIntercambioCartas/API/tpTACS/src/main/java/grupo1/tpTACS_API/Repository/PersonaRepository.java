package grupo1.tpTACS_API.Repository;


import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Entity.RolesPersona;
import org.springframework.stereotype.Repository;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PersonaRepository {
    private Map<String, Persona> tablaPersona = new HashMap<>();
    private AtomicInteger idPersona = new AtomicInteger(0);


    public PersonaRepository() {

    }

    public void clear(){
        tablaPersona.clear();
    }

    public Persona guardarPersona (Persona persona){
        persona.setId(idPersona.getAndIncrement());
        tablaPersona.put(persona.getUsername(), persona);
        return persona;
    }

    public Persona getPersona (String username) {
        return tablaPersona.get(username);
    }

    public RolesPersona getRol (String username) {
        return tablaPersona.get(username).getRol();
    }

    public Optional<Persona> findByNombre (String nombre) {
        return Optional.ofNullable(tablaPersona.get(nombre));
    }
}

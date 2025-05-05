package grupo1.tpTACS_API.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class Persona implements UserDetails {
    private int id;
    private String nombre;
    private String contraseniaHasheada;
    private RolesPersona rol;

    public Persona(String nombre, String contraseniaHasheada, RolesPersona rol) {
        this.nombre = nombre;
        this.contraseniaHasheada = contraseniaHasheada;
        this.rol = rol;
    }

    public Persona(String nombre, RolesPersona rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    //Funciones de UserDetails. Es necesaria esta implementacion para el uso de Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.toString()));
    }

    @Override
    public String getPassword() {
        return this.contraseniaHasheada;
    }

    @Override
    public String getUsername() {
        return this.nombre;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

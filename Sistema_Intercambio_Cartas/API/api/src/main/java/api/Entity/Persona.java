package api.Entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Document(collection = "personas")
@NoArgsConstructor
public class Persona implements UserDetails {
    @Id
    private String id;
    private String nombre;
    private String contraseniaHasheada;
    private RolesPersona rol;
    private LocalDate fechaCreacion;

    public Persona(String nombre, String contraseniaHasheada, RolesPersona rol) {
        this.nombre = nombre;
        this.contraseniaHasheada = contraseniaHasheada;
        this.rol = rol;
        this.fechaCreacion = LocalDate.now();
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

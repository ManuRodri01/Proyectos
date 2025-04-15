package manuRodri.intercambioCartas.Config;

import manuRodri.intercambioCartas.Service.ImplUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final ImplUserDetailsService userDetailsService;

    public ApplicationConfig(ImplUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Crea el AuthenticationManager utilizado para la autenticación de usuarios durante
     * el inicio de sesión
     * @param config es el objeto dado por Spring para la configuración de la autenticación
     * @return El AuthenticationManager ya configurado y listo para realizar la autenticación
     * @throws Exception en caso de que ocurra un error durante la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Crea y configura un AuthenticationProvider para la autenticación de Usuarios en
     * la base de datos, con su nombre y contraseña.
     * @param userDetailsService El servicio que obtiene la información de los usuarios desde la base de datos
     * @param passwordEncoder El codificador de contraseñas utilizado para comparar las contraseñas (ya que están encriptadas)
     * @return Un AuthenticationProvider ya configurado para la autenticación
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //Utilizado para encriptar la contraseña con un salt
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}

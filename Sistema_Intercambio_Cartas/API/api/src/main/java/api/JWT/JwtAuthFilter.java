package api.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
//Esta clase se va a encargar de autenticar las request con JWT.
//Extiende de OncePerRequestFilter, lo que indica que lo debe hacer solo una vez
//por request
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtra la solicitud HTTP para manejar la autenticación basada en JWT
     *
     * Intercepta la request entrante, extrae el token JWT, lo valida y si es correcto
     * auténtica al usuario en el contexto de seguridad de Spring. En caso de que no
     * exista el token o sea inválido, continua con la cadena de filtros sin establecer
     * la autenticación
     * @param request la solicitud HTTP entrante de donde se extraera el token
     * @param response la respuesta HTTP que se genera
     * @param filterChain la cadena de filtros para continuar aplicando los filtros
     * @throws ServletException en caso de que suceda un error en la logica del filtro
     * @throws IOException en caso de que ocurra un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String username;

        if (token == null) {
            //Si el token es nulo, la cadena de filtros puede seguir haciendo su trabajo
            filterChain.doFilter(request, response);
            return;
        }

        username = jwtService.usernameDesdeToken(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(jwtService.esTokenValido(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
                        (userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                //Crea una instancia de Authentication, guardando el usuario,
                //sin guardar las credenciales (xq no se requieren a esta altura) y le
                //agrega info adicional de la solicitud (Eso es lo que hace setDetails)
                //Finalmente, le agrega al SecurityContext esta Authentication para que pueda
                //ser usada por el resto de los controllers durante esta request
            }
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Obtiene el token JWT desde el encabezado de la solicitud HTTP
     *
     * Verifica que exista el token y tenga el prefijo correcto, devolviendo null
     * en caso de que no suceda.
     * @param request la solicitud http de donde se obtiene el token
     * @return el token JWT sin el prefijo Bearer o null si no existe o no es válido
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        final String encabezado = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(encabezado) && encabezado.startsWith("Bearer ")) {
            //Comprueba que el encabezado no este vacio y que sea un encabezado valido
            //Luego elimina la parte de Bearer para dejar solo el token
            return encabezado.substring(7);
        }

        return null;

    }
}

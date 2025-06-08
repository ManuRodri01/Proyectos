package api.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.stream;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String llave_secreta;

    /**
     * Genera un token JWT para un usuario autenticado
     *
     *
     * El metodo genera un token JWT que incluye el nombre del usuario, su rol,
     * la fecha de creación y la fecha de expiración (un día después de la de creación).
     * El token es firmado utilizando una clave secreta y el algoritmo HS256.
     * @param persona el usuario para el que se genera el token
     * @return el token JWT como String
     */
    public String generarToken(UserDetails persona) {
        return Jwts.builder()
                .setSubject(persona.getUsername())
                .claim("rol", persona.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))//Un dia desde la creacion
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        /*
            De esta forma se crea el token. Los claims son los campos de datos del token.
            Luego seteamos a quien pertence el token con el nombre de usuario,
            el momento en el que se genero y la fecha de expiracion siendo un dia dsp
            de la creacion del mismo.
            Seteamos un claim personalizado donde se le pasa el rol de la persona y si no
            lo tiene, le setea una como usuario
            Finalmente, lo firma digitalmente utilizando una clave y un algoritmo. Esta firma
            nos asegura que el token no haya sido modificado y que haya sido generado por mi
         */
    }

    /**
     * Genera una clave con base en un String secreto codificado en base 64,
     * utilizando el algoritmo HMAC SHA
     * @return una Key para firmar/verificar tokens
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(llave_secreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String username = usernameDesdeToken(token);
        return (username.equals(userDetails.getUsername()) && !estaTokenExpirado(token));
    }

    public String usernameDesdeToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.
                parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generarTokenExpirado(UserDetails persona) {
        LocalDateTime ayerLocalDateTime = LocalDateTime.now().minusDays(1);
        Date ayer = Date.from(ayerLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(persona.getUsername())
                .claim("rol", persona.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(ayer)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private boolean estaTokenExpirado(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }
}

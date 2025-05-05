package tpTacs.Bot.Service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tpTacs.Bot.DTO.*;
import tpTacs.Bot.FiltrosPublicacion;
import tpTacs.Bot.RolesPersona;

import java.util.List;

@Service
public class BackEndClient {
    private final RestTemplate restTemplate;

    public BackEndClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthResponse login(String username, String password) {
        String url = "http://localhost:8080/auth/login";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(username, password);
        return restTemplate.postForEntity(url, loginRequestDTO, AuthResponse.class).getBody();

    }

    public AuthResponse register(String username, String password, RolesPersona rolPersona) {
        String url = "http://localhost:8080/auth/register";
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(username, password, rolPersona);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(url, registerRequestDTO, AuthResponse.class);
        return response.getBody();
    }

    public PersonaDTO getPersona(String token) {
        String url = "http://localhost:8080/personas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, PersonaDTO.class).getBody();
    }

    public void crearCarta(String token, String nombreCarta ,Integer juegoId) {
        String url = "http://localhost:8080/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionCartaDTO cartaDTO = new CreacionCartaDTO(nombreCarta, juegoId);
        HttpEntity<CreacionCartaDTO> entity = new HttpEntity<>(cartaDTO, headers);
        restTemplate.postForEntity(url, entity, CreacionCartaDTO.class);
    }

    public void eliminarCarta(String token, Integer cartaId) {
        String url = "http://localhost:8080/cartas/"+cartaId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public void crearJuego(String token, String nombreJuego) {
        String url = "http://localhost:8080/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionJuegoDTO juegoDTO = new CreacionJuegoDTO(nombreJuego);
        HttpEntity<CreacionJuegoDTO> entity = new HttpEntity<>(juegoDTO, headers);
        restTemplate.postForEntity(url, entity, CreacionCartaDTO.class);
    }

    public void eliminarJuego(String token, Integer juegoId) {
        String url = "http://localhost:8080/juegos/"+ juegoId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public List<PublicacionPersonaDTO> getPublicacionesPersona(String token) {
        String url = "http://localhost:8080/personas/publicaciones";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<PublicacionPersonaDTO>>(){}).getBody();
    }

    public RespuestaPublicacionDTO getPublicacion(String token, int idPublicacion) {
        String url = "http://localhost:8080/publicaciones/" + idPublicacion;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, RespuestaPublicacionDTO.class).getBody();
    }

    public void borrarPublicacion(String token, int idPublicacion) {
        String url = "http://localhost:8080/publicaciones/" + idPublicacion;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public List<OfertaPersonaDTO> getOfertasRecibidas(String token) {
        String url = "http://localhost:8080/personas/ofertas-recibidas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfertaPersonaDTO>>(){}).getBody();
    }

    public List<OfertaPersonaDTO> getOfertasHechas(String token) {
        String url = "http://localhost:8080/personas/ofertas-hechas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfertaPersonaDTO>>(){}).getBody();
    }

    public RespuestaOfertaDTO getOferta(String token, int idOferta) {
        String url = "http://localhost:8080/ofertas/" + idOferta;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, RespuestaOfertaDTO.class).getBody();
    }

    public void crearOferta(String token, CreacionOferta oferta) {
        String url = "http://localhost:8080/ofertas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<CreacionOferta> entity = new HttpEntity<>(oferta, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void aceptarOferta(String token, int idOferta) {
        String url = "http://localhost:8080/ofertas/" + idOferta + "/aceptar";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void rechazarOferta(String token, int idOferta) {
        String url = "http://localhost:8080/ofertas/" + idOferta + "/rechazar";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void crearPublicacion(String token, CreacionPublicacionDTO publicacion) {
        String url = "http://localhost:8080/publicaciones";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<CreacionPublicacionDTO> entity = new HttpEntity<>(publicacion, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public PaginacionPublicacionDTO getPublicaciones(String token, int pagina, FiltrosPublicacion filtros) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/publicaciones");
        builder.queryParam("pagina", pagina);
        if(filtros.getCartaId() != null) {
            builder.queryParam("cartaId", filtros.getCartaId());
        }
        if(filtros.getJuegoId() != null) {
            builder.queryParam("juegoId", filtros.getJuegoId());
        }
        if(filtros.getNombrePublicacion() != null) {
            builder.queryParam("nombrePublicacion", filtros.getNombrePublicacion().replaceAll("\\s+", "-"));
        }
        if(filtros.getEstadoConservacion() != null) {
            builder.queryParam("estadoConservacion", filtros.getEstadoConservacion());
        }
        if(filtros.getDesdeFecha() != null) {
            builder.queryParam("desdeFecha", filtros.getDesdeFecha());
        }
        if(filtros.getHastaFecha() != null) {
            builder.queryParam("hastaFecha", filtros.getHastaFecha());
        }
        if(filtros.getDesdePrecio() != null) {
            builder.queryParam("desdePrecio", filtros.getDesdePrecio());
        }
        if(filtros.getHastaPrecio() != null) {
            builder.queryParam("hastaPrecio", filtros.getHastaPrecio());
        }
        if(filtros.getIntereses() != null) {
            for(Integer interes: filtros.getIntereses()){
                if(interes != null) {
                    builder.queryParam("intereses", interes);
                }
            }
        }
        String url = builder.build().toUriString();
        System.out.println(url);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<FiltrosPublicacion> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, PaginacionPublicacionDTO.class).getBody();
    }

    public List<JuegoDTO> getJuegos(String token) {
        String url = "http://localhost:8080/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<JuegoDTO>>(){}).getBody();
    }

    public JuegoDTO getJuego(String token, int idJuego) {
        String url = "http://localhost:8080/juegos/" + idJuego;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, JuegoDTO.class).getBody();
    }

    public List<CartaDTO> getCartasByJuego(String token, int idJuego) {
        String url = "http://localhost:8080/cartas/juego/" + idJuego;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CartaDTO>>(){}).getBody();
    }

    public List<CartaDTO> getCartas(String token) {
        String url = "http://localhost:8080/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CartaDTO>>(){}).getBody();
    }

    public List<String> getEstados(String token) {
        String url = "http://localhost:8080/cartas/estados";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {}).getBody();
    }
}

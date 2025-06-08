package bot.Service;

import bot.DTO.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import bot.FiltrosPublicacion;
import bot.RolesPersona;

import java.util.List;

@Service
public class BackEndClient {
    private final RestTemplate restTemplate;

    //@Value("${backend.url}")
    private String baseUrl = "http://backend:8080";

    public BackEndClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthResponse login(String username, String password) {
        String url = baseUrl + "/auth/login";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(username, password);
        return restTemplate.postForEntity(url, loginRequestDTO, AuthResponse.class).getBody();
    }

    public AuthResponse register(String username, String password, RolesPersona rolPersona) {
        String url = baseUrl + "/auth/register";
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(username, password, rolPersona);
        return restTemplate.postForEntity(url, registerRequestDTO, AuthResponse.class).getBody();
    }

    public PersonaDTO getPersona(String token) {
        String url = baseUrl + "/personas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, PersonaDTO.class).getBody();
    }

    public void crearCarta(String token, String nombreCarta ,String juegoId) {
        String url = baseUrl + "/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionCartaDTO cartaDTO = new CreacionCartaDTO(nombreCarta, juegoId);
        HttpEntity<CreacionCartaDTO> entity = new HttpEntity<>(cartaDTO, headers);
        restTemplate.postForEntity(url, entity, CreacionCartaDTO.class);
    }

    public void eliminarCarta(String token, String cartaId) {
        String url = baseUrl + "/cartas/" + cartaId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public void crearJuego(String token, String nombreJuego) {
        String url = baseUrl + "/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionJuegoDTO juegoDTO = new CreacionJuegoDTO(nombreJuego);
        HttpEntity<CreacionJuegoDTO> entity = new HttpEntity<>(juegoDTO, headers);
        restTemplate.postForEntity(url, entity, CreacionCartaDTO.class);
    }

    public void eliminarJuego(String token, String juegoId) {
        String url = baseUrl + "/juegos/" + juegoId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public List<PublicacionPersonaDTO> getPublicacionesPersona(String token) {
        String url = baseUrl + "/personas/publicaciones";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<PublicacionPersonaDTO>>(){}).getBody();
    }

    public RespuestaPublicacionDTO getPublicacion(String token, String idPublicacion) {
        String url = baseUrl + "/publicaciones/" + idPublicacion;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, RespuestaPublicacionDTO.class).getBody();
    }

    public void borrarPublicacion(String token, String idPublicacion) {
        String url = baseUrl + "/publicaciones/" + idPublicacion;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public List<OfertaPersonaDTO> getOfertasRecibidas(String token) {
        String url = baseUrl + "/personas/ofertas-recibidas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfertaPersonaDTO>>(){}).getBody();
    }

    public List<OfertaPersonaDTO> getOfertasHechas(String token) {
        String url = baseUrl + "/personas/ofertas-hechas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfertaPersonaDTO>>(){}).getBody();
    }

    public RespuestaOfertaDTO getOferta(String token, String idOferta) {
        String url = baseUrl + "/ofertas/" + idOferta;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, RespuestaOfertaDTO.class).getBody();
    }

    public void crearOferta(String token, CreacionOferta oferta) {
        String url = baseUrl + "/ofertas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<CreacionOferta> entity = new HttpEntity<>(oferta, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void aceptarOferta(String token, String idOferta) {
        String url = baseUrl + "/ofertas/" + idOferta + "/aceptar";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void rechazarOferta(String token, String idOferta) {
        String url = baseUrl + "/ofertas/" + idOferta + "/rechazar";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void crearPublicacion(String token, CreacionPublicacionDTO publicacion) {
        String url = baseUrl + "/publicaciones";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<CreacionPublicacionDTO> entity = new HttpEntity<>(publicacion, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    /**
     * Obtiene una lista paginada de publicaciones desde el backend, aplicando filtros opcionales.
     * <p>
     * Este método construye una URL con los parámetros de consulta para la paginación y los filtros aplicados
     * (como carta, juego, nombre de publicación, estado de conservación, fechas y precios) y realiza una solicitud HTTP GET
     * al backend para obtener las publicaciones. Además, se incluye un token de autenticación en los encabezados de la solicitud.
     *
     * @param token El token de autenticación JWT que se utiliza para autorizar la solicitud al backend.
     * @param pagina El número de la página de resultados que se desea obtener.
     * @param filtros Un objeto FiltrosPublicacion que contiene los filtros a aplicar en la búsqueda de publicaciones.
     *
     * @return Un objeto PaginacionPublicacionDTO que contiene la lista de publicaciones correspondientes a la página solicitada,
     *         junto con los metadatos de paginación.
     */
    public PaginacionPublicacionDTO getPublicaciones(String token, int pagina, FiltrosPublicacion filtros) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/publicaciones")
            .queryParam("pagina", pagina);
        if(filtros.getCartaId() != null) builder.queryParam("cartaId", filtros.getCartaId());
        if(filtros.getJuegoId() != null) builder.queryParam("juegoId", filtros.getJuegoId());
        if(filtros.getNombrePublicacion() != null) builder.queryParam("nombrePublicacion", filtros.getNombrePublicacion().replaceAll("\\s+", "-"));
        if(filtros.getEstadoConservacion() != null) builder.queryParam("estadoConservacion", filtros.getEstadoConservacion());
        if(filtros.getDesdeFecha() != null) builder.queryParam("desdeFecha", filtros.getDesdeFecha());
        if(filtros.getHastaFecha() != null) builder.queryParam("hastaFecha", filtros.getHastaFecha());
        if(filtros.getDesdePrecio() != null) builder.queryParam("desdePrecio", filtros.getDesdePrecio());
        if(filtros.getHastaPrecio() != null) builder.queryParam("hastaPrecio", filtros.getHastaPrecio());
        if(filtros.getIntereses() != null) for(String interes: filtros.getIntereses()) if(interes != null) builder.queryParam("intereses", interes);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<FiltrosPublicacion> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET, entity, PaginacionPublicacionDTO.class).getBody();
    }

    public List<JuegoDTO> getJuegos(String token) {
        String url = baseUrl + "/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<JuegoDTO>>(){}).getBody();
    }

    public JuegoDTO getJuego(String token, String idJuego) {
        String url = baseUrl + "/juegos/" + idJuego;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, JuegoDTO.class).getBody();
    }

    public List<CartaDTO> getCartasByJuego(String token, String idJuego) {
        String url = baseUrl + "/cartas/juego/" + idJuego;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CartaDTO>>(){}).getBody();
    }

    public List<CartaDTO> getCartas(String token) {
        String url = baseUrl + "/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CartaDTO>>(){}).getBody();
    }

    public List<String> getEstados(String token) {
        String url = baseUrl + "/cartas/estados";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {}).getBody();
    }

    public void crearSolicitudJuego(String token, String nombreJuego){
        String url = baseUrl + "/solicitudes/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionSolicitudJuegoDTO solicitudJuegoDTO = new CreacionSolicitudJuegoDTO(nombreJuego);
        HttpEntity<CreacionSolicitudJuegoDTO> entity = new HttpEntity<>(solicitudJuegoDTO,headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void crearSolicitudCarta(String token, String nombreCarta, String idJuego){
        String url = baseUrl + "/solicitudes/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        CreacionSolicitudCartaDTO solicitudCartaDTO = new CreacionSolicitudCartaDTO(nombreCarta, idJuego);
        HttpEntity<CreacionSolicitudCartaDTO> entity = new HttpEntity<>(solicitudCartaDTO,headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public List<SolicitudJuegoDTO> getTodasSolicitudJuego(String token){
        String url = baseUrl + "/solicitudes/juegos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudJuegoDTO>>(){}).getBody();
    }

    public List<SolicitudJuegoDTO> getSolicitudesJuegoEnEspera(String token){
        String url = baseUrl + "/solicitudes/juegos/en_espera";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudJuegoDTO>>(){}).getBody();
    }

    public List<SolicitudCartaDTO> getTodasSolicitudCarta(String token){
        String url = baseUrl + "/solicitudes/cartas";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudCartaDTO>>(){}).getBody();
    }

    public List<SolicitudCartaDTO> getSolicitudesCartaEnEspera(String token){
        String url = baseUrl + "/solicitudes/cartas/en_espera";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudCartaDTO>>(){}).getBody();
    }

    public SolicitudJuegoDTO getSolicitudJuego(String token, String idSolicitud){
        String url = baseUrl + "/solicitudes/juegos/" + idSolicitud;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, SolicitudJuegoDTO.class).getBody();
    }

    public SolicitudCartaDTO getSolicitudCarta(String token, String idSolicitud){
        String url = baseUrl + "/solicitudes/cartas/" + idSolicitud;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, SolicitudCartaDTO.class).getBody();
    }

    public List<SolicitudCartaDTO> getSolicitudesCartaByPersona(String token){
        String url = baseUrl + "/solicitudes/cartas/persona";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudCartaDTO>>(){}).getBody();
    }

    public List<SolicitudJuegoDTO> getSolicitudesJuegoByPersona(String token){
        String url = baseUrl + "/solicitudes/juegos/persona";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SolicitudJuegoDTO>>(){}).getBody();
    }

    public void administrarSolicitudJuego(String token, String estado, String idSolicitud){
        String url = baseUrl + "/solicitudes/juegos/" + idSolicitud + "/estado-aprobacion";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        EstadoSolicitudDTO estadoSolicitudDTO = new EstadoSolicitudDTO(estado);
        HttpEntity<EstadoSolicitudDTO> entity = new HttpEntity<>(estadoSolicitudDTO,headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void administrarSolicitudCarta(String token, String estado, String idSolicitud){
        String url = baseUrl + "/solicitudes/cartas/" + idSolicitud + "/estado-aprobacion";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        EstadoSolicitudDTO estadoSolicitudDTO = new EstadoSolicitudDTO(estado);
        HttpEntity<EstadoSolicitudDTO> entity = new HttpEntity<>(estadoSolicitudDTO,headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }
}

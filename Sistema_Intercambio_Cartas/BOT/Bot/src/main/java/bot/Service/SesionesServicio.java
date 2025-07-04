package bot.Service;

import org.springframework.stereotype.Service;
import bot.BotState;
import bot.CartaNumId;
import bot.DTO.CreacionOferta;
import bot.DTO.CreacionPublicacion;
import bot.FiltrosPublicacion;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SesionesServicio {
    private final Map<Long, String> userSesiones = new HashMap<>();
    private final Map<Long, String> usersTokens = new HashMap<>();
    private final Map<Long, BotState> chatEstados = new HashMap<>();
    private final Map<Long, String> ultimaPublicacion = new HashMap<>();
    private final Map<Long, CreacionOferta> ofertasUsuario = new HashMap<>();
    private final Map<Long, FiltrosPublicacion> filtrosActivos = new HashMap<>();
    private final Map<Long, String> adminUltimoJuego = new HashMap<>();
    private final Map<Long, String> creacionAdmin = new HashMap<>();
    private final Map<Long, CreacionPublicacion> creacionPublicacionMap = new HashMap<>();
    private final Map<Long, List<CartaNumId>> listaCartas = new HashMap<>();
    private final Map<Long, String> idJuegoSolicitudCarta = new HashMap<>();

    public SesionesServicio() {

    }

    public String obtenerUltimoJuego(Long chatId) {
        return adminUltimoJuego.get(chatId);
    }

    public String obtenerNombreCreacionAdmin(Long chatId) {
        return creacionAdmin.get(chatId);
    }

    public String obtenerNombre(Long chatId) {
        return userSesiones.get(chatId);
    }

    public String obtenerToken(Long chatId) {
        return usersTokens.get(chatId);
    }

    public BotState obtenerEstadoConversacion(Long chatId) {
        return chatEstados.getOrDefault(chatId, BotState.INICIO);
    }

    public CreacionOferta obtenerUltimaOferta(Long chatId) {
        return ofertasUsuario.getOrDefault(chatId, new CreacionOferta());
    }

    public FiltrosPublicacion obtenerFiltros(Long chatId) {
        return filtrosActivos.getOrDefault(chatId, new FiltrosPublicacion());
    }

    public CreacionPublicacion obtenerCreacionPublicacion(Long chatId) {
        return creacionPublicacionMap.getOrDefault(chatId, new CreacionPublicacion());
    }

    public List<CartaNumId> obtenerCartas(Long chatId) {
        return listaCartas.get(chatId);
    }

    public String obtenerIdJuegoSolicitudCarta(Long chatId) {
        return idJuegoSolicitudCarta.get(chatId);
    }

    public void guardarUltimoJuego(Long chatId, String juegoId) {
        adminUltimoJuego.put(chatId, juegoId);
    }

    public void guardarNombreCreacionAdmin(Long chatId, String nombreCreacionAdmin) {
        creacionAdmin.put(chatId, nombreCreacionAdmin);
    }

    public void guardarEstadoConversacion(Long chatId, BotState estadoConversacion) {
        chatEstados.put(chatId, estadoConversacion);
    }

    public void guardarUltimaOferta(Long chatId, CreacionOferta ultimaOferta) {
        ofertasUsuario.put(chatId, ultimaOferta);
    }

    public void guardarPubliIdOferta(Long chatId, String publicacionId) {
        CreacionOferta oferta = ofertasUsuario.get(chatId);
        oferta.setPublicacionId(publicacionId);
    }

    public void guardarValorUltimaOferta(Long chatId, Float precioUltima) {
        CreacionOferta oferta = ofertasUsuario.get(chatId);
        oferta.setValor(precioUltima);

    }

    public void guardarCartasOferta(Long chatId, List<String> cartas) {
        CreacionOferta oferta = ofertasUsuario.get(chatId);
        oferta.setCartas(cartas);
    }

    public void guardarToken(Long chatId, String token) {
        usersTokens.put(chatId, token);
    }

    public void guardarNombre(Long chatId, String nombre) {
        userSesiones.put(chatId, nombre);
    }

    public void guardarFiltros(Long chatId, FiltrosPublicacion filtros) {
        filtrosActivos.put(chatId, filtros);
    }

    public void guardarJuego(Long chatId, String idJuego) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setJuegoId(idJuego);
    }

    public void guardarCarta(Long chatId, String cartaId) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setCartaId(cartaId);
    }

    public void guardarNombreFiltro(Long chatId, String nombreFiltro) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setNombrePublicacion(nombreFiltro);
    }

    public void guardarEstadoFiltro(Long chatId, String estadoFiltro) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setEstadoConservacion(estadoFiltro);
    }

    public void guardarFechaInicio(Long chatId, LocalDate fechaInicio) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setDesdeFecha(fechaInicio.atStartOfDay());
    }

    public void guardarFechaFinal(Long chatId, LocalDate fechaFinal) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setHastaFecha(fechaFinal.atStartOfDay());
    }

    public void guardarPrecioInicial(Long chatId, float precioInicial) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setDesdePrecio(precioInicial);
    }

    public void guardarPrecioFinal(Long chatId, float precioFinal) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setHastaPrecio(precioFinal);
    }

    public void guardarIntereses(Long chatId, List<String> intereses) {
        FiltrosPublicacion filtros = filtrosActivos.get(chatId);
        filtros.setIntereses(intereses);
    }

    public void guardarPublicacion(Long chatId, CreacionPublicacion publicacion) {
        creacionPublicacionMap.put(chatId, publicacion);
    }

    public void guardarJuegoPublicacion(Long chatId, String idJuego) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setJuegoId(idJuego);
    }

    public void guardarCartaPublicacion(Long chatId, String cartaId) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setCartaId(cartaId);
    }

    public void guardarEstadoPublicacion(Long chatId, String estadoPublicacion) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setEstadoConservacion(estadoPublicacion);
    }

    public void guardarTituloPublicacion(Long chatId, String tituloPublicacion) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setTitulo(tituloPublicacion);
    }

    public void guardarDescripcionPublicacion(Long chatId, String descripcionPublicacion) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setDescripcion(descripcionPublicacion);
    }

    public void guardarValorPublicacion(Long chatId, float valorPublicacion) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setPrecio(valorPublicacion);
    }

    public void guardarInteresesPublicacion(Long chatId, List<String> intereses) {
        CreacionPublicacion publicacion = creacionPublicacionMap.get(chatId);
        publicacion.setIntereses(intereses);
    }

    public void guardarListaCartas(Long chatId, List<CartaNumId> cartas) {
        listaCartas.put(chatId, cartas);
    }

    public void guardarIdJuegoSolicitudCarta(Long chatId, String idJuego) {
        idJuegoSolicitudCarta.put(chatId, idJuego);
    }

    public void limpiarFiltros(Long chatId) {
        filtrosActivos.remove(chatId);
    }

    public void limpiarOferta(Long chatId) {
        ofertasUsuario.remove(chatId);
    }

    public void limpiarPublicacion(Long chatId) {
        creacionPublicacionMap.remove(chatId);
    }

    public void limpiarListaCartas(Long chatId) {
        listaCartas.remove(chatId);
    }

    public void limpiarIdJuegoSolicitudCarta(Long chatId) {
        idJuegoSolicitudCarta.remove(chatId);
    }

    public void limpiar(Long chatId) {
        userSesiones.remove(chatId);
        usersTokens.remove(chatId);
        chatEstados.remove(chatId);
        ultimaPublicacion.remove(chatId);
        creacionPublicacionMap.remove(chatId);
        creacionAdmin.remove(chatId);
        adminUltimoJuego.remove(chatId);
        filtrosActivos.remove(chatId);
        ofertasUsuario.remove(chatId);
        listaCartas.remove(chatId);
        idJuegoSolicitudCarta.remove(chatId);
    }
}

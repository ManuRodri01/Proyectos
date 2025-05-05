package tpTacs.Bot.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tpTacs.Bot.BotState;
import tpTacs.Bot.DTO.*;
import tpTacs.Bot.FiltrosPublicacion;
import tpTacs.Bot.RolesPersona;
import tpTacs.Bot.Teclados.*;
import tpTacs.Bot.TelegramBot;

import tpTacs.Bot.DTO.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramBotService {
    private final SesionesServicio sesionesServicio;
    private final BackEndClient backEndClient;
    private TelegramBot bot;

    public TelegramBotService(TelegramBot bot) {
        this.sesionesServicio = new SesionesServicio();
        this.backEndClient = new BackEndClient(new RestTemplate());
        this.bot = bot;
    }

    public void handleMessage(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        BotState estado = sesionesServicio.obtenerEstadoConversacion(chatId);

        switch (estado) {
            case ESPERANDO_NOMBRE_LOGIN:
                sesionesServicio.guardarNombre(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_LOGIN);
                enviarMensaje(chatId, "Ingrese su contraseña:");
                break;

            case ESPERANDO_CONTRASENIA_LOGIN:
                handleLogin(chatId, text);
                break;

            case ESPERANDO_NOMBRE_REGISTER:
                sesionesServicio.guardarNombre(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_REGISTER);
                enviarMensaje(chatId, "Ingrese su contraseña:");
                break;

            case ESPERANDO_CONTRASENIA_REGISTER:
                handleRegister(chatId, text);
                break;
            case INICIO:
                sesionesServicio.limpiar(chatId);
                handleInicio(chatId);
                break;

            case ESPERANDO_NOMBRE_CREACION_CARTA:
                handleCrearCarta(chatId, text);
                break;

            case ESPERANDO_NOMBRE_CREACION_JUEGO:
                handleCrearJuego(chatId, text);
                break;

            case ESPERANDO_NOMBRE_CREACION_ADMIN:
                sesionesServicio.guardarNombreCreacionAdmin(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_CREACION_ADMIN);
                enviarMensaje(chatId, "Ingrese la contraseña del nuevo admin");
                break;

            case ESPERANDO_CONTRASENIA_CREACION_ADMIN:
                handleCrearAdmin(chatId, text);
                break;

            case ESPERANDO_TITULO_PUBLICACION:
                sesionesServicio.guardarTituloPublicacion(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_DESCRIPCION_PUBLICACION);
                enviarMensaje(chatId, "Ingrese la descripcion de la publicacion");
                break;

            case ESPERANDO_DESCRIPCION_PUBLICACION:
                sesionesServicio.guardarDescripcionPublicacion(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_VALOR_PUBLICACION);
                enviarMensaje(chatId, "Desea ingresar un valor estimado de la carta?", TecladoCreacionPublicacion.get("VALOR"));
                break;

            case ESPERANDO_VALOR_PUBLICACION:
                try{
                    float valorEstimado = Float.parseFloat(text);
                    sesionesServicio.guardarValorPublicacion(chatId, valorEstimado);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_INTERESES_PUBLICACION);
                    enviarMensaje(chatId, "Desea ingresar las cartas que le interesan a cambio de esta carta?", TecladoCreacionPublicacion.get("INTERESES"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto");
                }
                break;

            case ESPERANDO_INTERESES_PUBLICACION:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> intereses = new ArrayList<>();
                    for (String part : parts) {
                        intereses.add(Integer.parseInt(part));
                    }
                    List<CartaDTO> cartas = backEndClient.getCartas(sesionesServicio.obtenerToken(chatId));
                    if(cartas.stream().map(CartaDTO::cartaId).toList().containsAll(intereses)){
                        sesionesServicio.guardarInteresesPublicacion(chatId, intereses);
                        handleCrearPublicacion(chatId);
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }
                catch (HttpClientErrorException e){

                }
                break;

            case ESPERANDO_SOLO_CARTAS_OFERTA:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> cartas = new ArrayList<>();
                    for (String part : parts) {
                        cartas.add(Integer.parseInt(part));
                    }
                    List<CartaDTO> cartasBack = backEndClient.getCartas(sesionesServicio.obtenerToken(chatId));
                    if(cartasBack.stream().map(CartaDTO::cartaId).toList().containsAll(cartas)){
                        sesionesServicio.guardarCartasOferta(chatId, cartas);
                        sesionesServicio.guardarValorUltimaOferta(chatId,-1f);
                        handleCrearOferta(chatId);
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }


                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }
                break;

            case ESPERANDO_SOLO_DINERO_OFERTA:
                try{
                    float precioOfertado = Float.parseFloat(text);
                    sesionesServicio.guardarValorUltimaOferta(chatId, precioOfertado);
                    sesionesServicio.guardarCartasOferta(chatId, new ArrayList<>());
                    handleCrearOferta(chatId);

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto");
                }
                break;

            case ESPERANDO_CARTAS_OFERTA:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> cartas = new ArrayList<>();
                    for (String part : parts) {
                        cartas.add(Integer.parseInt(part));
                    }
                    List<CartaDTO> cartasBack = backEndClient.getCartas(sesionesServicio.obtenerToken(chatId));
                    if(cartasBack.stream().map(CartaDTO::cartaId).toList().containsAll(cartas)){
                        sesionesServicio.guardarCartasOferta(chatId, cartas);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_DINERO_OFERTA);
                        enviarMensaje(chatId,"Ingrese el valor que quiere ofrecer, utilice puntos para las unidades decimales");
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }


                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }
                break;

            case ESPERANDO_DINERO_OFERTA:
                try{
                    float precioOfertado = Float.parseFloat(text);
                    sesionesServicio.guardarValorUltimaOferta(chatId, precioOfertado);
                    handleCrearOferta(chatId);

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto");
                }
                break;

            case ESPERANDO_FILTRO_NOMBRE:
                sesionesServicio.guardarNombreFiltro(chatId, text);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_ESTADO);
                enviarMensaje(chatId, "Desea aplicar el filtro de estado conservacion de la carta?", TecladoFiltro.get("ESTADO"));
                break;

            case ESPERANDO_FILTRO_FECHA_INICIO:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try{
                    LocalDate fechaInicio = LocalDate.parse(text, formatter);
                    sesionesServicio.guardarFechaInicio(chatId, fechaInicio);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_FINAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de fecha final?", TecladoFiltro.get("FECHA_FINAL"));

                }
                catch (DateTimeParseException e){
                    enviarMensaje(chatId, "Por favor ingrese la fecha en el formato DD/MM/yyyy con valores posibles");
                }
                break;
            case ESPERANDO_FILTRO_FECHA_FINAL:
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try{
                    LocalDate fechaFinal = LocalDate.parse(text, formatter2);
                    sesionesServicio.guardarFechaFinal(chatId, fechaFinal);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_INICIAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de precio minimo?", TecladoFiltro.get("PRECIO_INICIAL"));

                }
                catch (DateTimeParseException e){
                    enviarMensaje(chatId, "Por favor ingrese la fecha en el formato DD/MM/yyyy con valores posibles");
                }
                break;
            case ESPERANDO_FILTRO_PRECIO_INICIAL:
                try{
                    float precioInicial = Float.parseFloat(text);
                    sesionesServicio.guardarPrecioInicial(chatId, precioInicial);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_FINAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de precio maximo?", TecladoFiltro.get("PRECIO_FINAL"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto");
                }
                break;
            case ESPERANDO_FILTRO_PRECIO_FINAL:
                try{
                    float precioFinal = Float.parseFloat(text);
                    sesionesServicio.guardarPrecioFinal(chatId, precioFinal);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_INTERESES);
                    enviarMensaje(chatId, "Desea aplicar el filtro de intereses de cartas?", TecladoFiltro.get("INTERESES"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto");
                }
                break;
            case ESPERANDO_FILTRO_INTERESES:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> intereses = new ArrayList<>();
                    for (String part : parts) {
                        intereses.add(Integer.parseInt(part));
                    }
                    List<CartaDTO> cartas = backEndClient.getCartas(sesionesServicio.obtenerToken(chatId));
                    if(cartas.stream().map(CartaDTO::cartaId).toList().containsAll(intereses)){
                        sesionesServicio.guardarIntereses(chatId, intereses);
                        handleVerPagina(chatId,1,sesionesServicio.obtenerFiltros(chatId));
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }
                catch (HttpClientErrorException e){

                }
                break;
            default:
                enviarMensaje(chatId, "No entiendo lo que estas diciendo. Si hay botones, usalos");
                break;

        }
    }

    public void handleCallBack(Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        if(data.contains("CREACION_CARTA_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.ESPERANDO_JUEGO_CREACION_CARTA){
                String [] parts = data.split("_");
                int juegoId = Integer.parseInt(parts[3]);
                sesionesServicio.guardarUltimoJuego(chatId, juegoId);
                sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_NOMBRE_CREACION_CARTA);
                enviarMensaje(chatId, "Escriba el nombre de la carta que quiere crear");
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ELIMINAR_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.ESPERANDO_SELECCION_CARTA_ELIMINACION){
                String [] parts = data.split("_");
                int cartaId = Integer.parseInt(parts[2]);
                handleEliminarCarta(chatId,cartaId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ELIMINAR_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.ESPERANDO_SELECCION_JUEGO_ELIMINACION){
                String [] parts = data.split("_");
                int juegoId = Integer.parseInt(parts[2]);
                handleEliminarJuego(chatId,juegoId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("CREACION_PUBLICACION_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_JUEGO_PUBLICACION){
                String [] parts = data.split("_");
                int juegoId = Integer.parseInt(parts[3]);
                sesionesServicio.guardarJuegoPublicacion(chatId, juegoId);
                enviarMensaje(chatId, "Seleccione la carta que quiere publicar");
                mostrarCartasCreacionPublicacion(chatId,juegoId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("PUBLICACION_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_CARTA_PUBLICACION){
                String [] parts = data.split("_");
                int cartaId = Integer.parseInt(parts[2]);
                sesionesServicio.guardarCartaPublicacion(chatId, cartaId);
                enviarMensaje(chatId, "Seleccione el estado de la carta");
                mostrarEstadosPublicacion(chatId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("PUBLICACION_ESTADO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_ESTADO_PUBLICACION){
                String [] parts = data.split("_");
                String estado = parts[2];
                sesionesServicio.guardarEstadoPublicacion(chatId, estado);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_TITULO_PUBLICACION);
                enviarMensaje(chatId, "Ingrese el titulo de la publicacion");
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("VER_PUBLICACION_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_PUBLICACION_PERFIL || sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_PUBLICACIONES){
                String [] parts = data.split("_");
                int idPublicacion= Integer.parseInt(parts[2]);
                handleVerPublicacion(chatId,idPublicacion);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;

        }
        if(data.contains("VER_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_OFERTA_PERFIL){
                String [] parts = data.split("_");
                int idOferta= Integer.parseInt(parts[2]);
                handleVerOferta(chatId,idOferta);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;

        }
        if(data.contains("REALIZAR_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.VIENDO_PUBLICACION) {
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId,BotState.CREANDO_OFERTA);
                String [] parts = data.split("_");
                int publicacionId = Integer.parseInt(parts[2]);
                CreacionOferta creacionOferta = new CreacionOferta();
                creacionOferta.setPublicacionId(publicacionId);
                sesionesServicio.guardarUltimaOferta(chatId,creacionOferta);
                enviarMensaje(chatId, "Que desea ofrecer para realizar la oferta?", TecladoCreacionOferta.get());
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ACEPTAR_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_OFERTA){
                String [] parts = data.split("_");
                int idOferta= Integer.parseInt(parts[2]);
                handleAceptarOferta(chatId,idOferta);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("RECHAZAR_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_OFERTA){
                String [] parts = data.split("_");
                int idOferta= Integer.parseInt(parts[2]);
                handleRechazarOferta(chatId,idOferta);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("BORRAR_PUBLICACION_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_PUBLICACION) {
                String [] parts = data.split("_");
                int idPubli= Integer.parseInt(parts[2]);
                handleBorrarPublicacion(chatId, idPubli);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("VER_PAGINA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_PUBLICACIONES){
                String [] parts = data.split("_");
                int pagina = Integer.parseInt(parts[2]);
                handleVerPagina(chatId,pagina,sesionesServicio.obtenerFiltros(chatId));
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("FILTRO_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_FILTRO_JUEGO) {
                String [] parts = data.split("_");
                int juegoId = Integer.parseInt(parts[2]);
                sesionesServicio.guardarJuego(chatId, juegoId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_CARTA);
                enviarMensaje(chatId, "Desea aplicar el filtro de carta?", TecladoFiltro.get("CARTA"));
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("FILTRO_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_FILTRO_CARTA) {
                String [] parts = data.split("_");
                int cartaId = Integer.parseInt(parts[2]);
                sesionesServicio.guardarCarta(chatId, cartaId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_NOMBRE);
                enviarMensaje(chatId, "Desea aplicar el filtro de nombre de publicacion?", TecladoFiltro.get("NOMBRE"));
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("FILTRO_ESTADO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_FILTRO_ESTADO) {
                String [] parts = data.split("_");
                String estado = parts[2];
                sesionesServicio.guardarEstadoFiltro(chatId, estado);
                sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_INICIO);
                enviarMensaje(chatId, "Desea aplicar el filtro de fecha inicial?", TecladoFiltro.get("FECHA_INICIO"));
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }


        switch (data) {
            case "LOGIN":
                if (sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.INICIO) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_NOMBRE_LOGIN);
                    enviarMensaje(chatId, "Ingrese su nombre de usuario");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "REGISTER":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.INICIO) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_NOMBRE_REGISTER);
                    enviarMensaje(chatId, "Ingrese su nombre de usuario");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CERRAR_SESION":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL || sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN) {
                    sesionesServicio.limpiar(chatId);
                    handleInicio(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL) {
                    handlePerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "MENU_PRINCIPAL_ADMIN":
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                break;

            case "ADMIN_CARTAS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ADMIN_CARTAS);
                    enviarMensaje(chatId, "Que desea hacer?", TecladoCartasAdmin.get());
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CREAR_CARTA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ADMIN_CARTAS) {
                    enviarMensaje(chatId, "Seleccione el juego al que pertenece la carta");
                    mostrarJuegosCreacionCarta(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "BORRAR_CARTA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ADMIN_CARTAS) {
                    mostrarCartasEliminacion(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "ADMIN_JUEGOS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ADMIN_JUEGOS);
                    enviarMensaje(chatId, "Que desea hacer?", TecladoJuegosAdmin.get());
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CREAR_JUEGO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ADMIN_JUEGOS) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_NOMBRE_CREACION_JUEGO);
                    enviarMensaje(chatId, "Ingrese el nombre del juego que quiere crear");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "BORRAR_JUEGO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ADMIN_JUEGOS) {
                    mostrarJuegosEliminacion(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CREAR_ADMIN":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN) {
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_NOMBRE_CREACION_ADMIN);
                    enviarMensaje(chatId, "Ingrese el nombre de usuario del nuevo admin");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "MENU_PRINCIPAL":
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                break;


            case "PUBLICACIONES_PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.PERFIL) {
                    handlePublicacionesPerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "OFERTAS_RECIBIDAS_PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.PERFIL) {
                    handleOfertasRecibidasPerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "OFERTAS_HECHAS_PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.PERFIL) {
                    handleOfertasHechasPerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "OFERTA_CARTAS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.CREANDO_OFERTA) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SOLO_CARTAS_OFERTA);
                    mostrarCartas(chatId);
                    enviarMensaje(chatId, "Ingrese los numeros de las cartas que le interese; todos en un mismo mensaje, separados por espacios");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "OFERTA_DINERO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.CREANDO_OFERTA) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SOLO_DINERO_OFERTA);
                    enviarMensaje(chatId, "Ingrese el valor que quiere ofrecer, utilice puntos para las unidades decimales");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "OFERTA_DINERO_CARTAS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.CREANDO_OFERTA) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CARTAS_OFERTA);
                    mostrarCartas(chatId);
                    enviarMensaje(chatId, "Ingrese los numeros de las cartas que le interese; todos en un mismo mensaje, separados por espacios");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CREAR_PUBLICACION":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL) {
                    sesionesServicio.limpiarPublicacion(chatId);
                    sesionesServicio.guardarPublicacion(chatId, new CreacionPublicacion());
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.CREANDO_PUBLICACION);
                    enviarMensaje(chatId, "Seleccione el juego al que pertenece la carta");
                    mostrarJuegosCreacionPublicacion(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SI_PUBLICACION_VALOR":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_VALOR_PUBLICACION){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_VALOR_PUBLICACION);
                    enviarMensaje(chatId, "Ingrese el valor estimado de la carta, utilice puntos para las unidades decimales");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "NO_PUBLICACION_VALOR":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_VALOR_PUBLICACION){
                    sesionesServicio.guardarValorPublicacion(chatId, -1);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_INTERESES_PUBLICACION);
                    enviarMensaje(chatId, "Desea ingresar las cartas que le interesan a cambio de esta carta?", TecladoCreacionPublicacion.get("INTERESES"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SI_PUBLICACION_INTERESES":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_INTERESES_PUBLICACION){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_INTERESES_PUBLICACION);
                    mostrarCartas(chatId);
                    enviarMensaje(chatId,"Ingrese los numeros de las cartas que le interese; todos en un mismo mensaje, separados por espacios");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "NO_PUBLICACION_INTERESES":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_INTERESES_PUBLICACION){
                    sesionesServicio.guardarInteresesPublicacion(chatId, new ArrayList<>());
                    handleCrearPublicacion(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "PUBLICACIONES":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL) {
                    sesionesServicio.limpiarFiltros(chatId);
                    handlePublicaciones(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "APLICAR_FILTROS":
                if (sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_PUBLICACIONES) {
                    sesionesServicio.guardarFiltros(chatId, new FiltrosPublicacion());
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_JUEGO);
                    enviarMensaje(chatId, "Desea aplicar el filtro de juego?", TecladoFiltro.get("JUEGO"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SI_FILTRO_JUEGO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_JUEGO) {
                    mostrarJuegos(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "NO_FILTRO_JUEGO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_JUEGO) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_CARTA);
                    enviarMensaje(chatId, "Desea aplicar el filtro de carta?", TecladoFiltro.get("CARTA"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SI_FILTRO_CARTA":
                if (sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_CARTA) {
                    mostrarCartasFiltro(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_CARTA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_CARTA) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_NOMBRE);
                    enviarMensaje(chatId, "Desea aplicar el filtro de nombre de publicacion?", TecladoFiltro.get("NOMBRE"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_NOMBRE":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_NOMBRE) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_NOMBRE);
                    enviarMensaje(chatId, "Ingrese el nombre de la publicacion");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_NOMBRE":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_NOMBRE) {
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_ESTADO);
                    enviarMensaje(chatId, "Desea aplicar el filtro de estado conservacion de la carta?", TecladoFiltro.get("ESTADO"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_ESTADO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_ESTADO) {
                    mostrarEstadosFiltros(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_ESTADO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_ESTADO) {
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_INICIO);
                    enviarMensaje(chatId, "Desea aplicar el filtro de fecha inicial?", TecladoFiltro.get("FECHA_INICIO"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_FECHA_INICIO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_INICIO){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_FECHA_INICIO);
                    enviarMensaje(chatId, "Ingrese la fecha inicial en el siguiente formato (DD/MM/YYYY)");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_FECHA_INICIO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_INICIO){
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_FINAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de fecha final?", TecladoFiltro.get("FECHA_FINAL"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_FECHA_FINAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_FINAL){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_FECHA_FINAL);
                    enviarMensaje(chatId, "Ingrese la fecha final en el siguiente formato (DD/MM/YYYY)");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_FECHA_FINAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_FECHA_FINAL){
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_INICIAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de precio minimo?", TecladoFiltro.get("PRECIO_INICIAL"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_PRECIO_INICIAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_INICIAL){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_PRECIO_INICIAL);
                    enviarMensaje(chatId, "Ingrese el precio minimo, utilice puntos para las unidades decimales");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_PRECIO_INICIAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_INICIAL){
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_FINAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de precio maximo?", TecladoFiltro.get("PRECIO_FINAL"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_PRECIO_FINAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_FINAL){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_PRECIO_FINAL);
                    enviarMensaje(chatId, "Ingrese el precio maximo, utilice puntos para las unidades decimales");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_PRECIO_FINAL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_FINAL){
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_INTERESES);
                    enviarMensaje(chatId, "Desea aplicar el filtro de intereses de cartas?", TecladoFiltro.get("INTERESES"));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "SI_FILTRO_INTERESES":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_INTERESES){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_INTERESES);
                    mostrarCartas(chatId);
                    enviarMensaje(chatId, "Ingrese los numeros de las cartas que le interese; todos en un mismo mensaje, separados por espacios");
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            case "NO_FILTRO_INTERESES":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_CONFIRMACION_FILTRO_INTERESES){
                    handleVerPagina(chatId,1,sesionesServicio.obtenerFiltros(chatId));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            default:
                handleInicio(chatId);

                break;
        }

    }

    private void handleInicio(Long chatId) throws TelegramApiException {
        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
        enviarMensaje(chatId, "Que desea hacer?", TecladoAuth.get());
    }

    private void enviarMensaje(Long chatId, String text) throws TelegramApiException {
        enviarMensaje(chatId, text, null);
    }

    private void enviarMensajeConFoto(Long chatId, String text, String imagen, InlineKeyboardMarkup teclado) throws TelegramApiException {
        if(!imagen.isEmpty()) {

            try{
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile(imagen));
                sendPhoto.setCaption(text);
                sendPhoto.setChatId(chatId);
                if(teclado != null) {
                    sendPhoto.setReplyMarkup(teclado);
                }


                bot.execute(sendPhoto);

            }
            catch(Exception e){
                if(!text.isEmpty()) {
                    enviarMensaje(chatId, text, teclado);
                }
            }
        }
        else if(!text.isEmpty()) {
            enviarMensaje(chatId, text, teclado);
        }
    }

    private void enviarMensaje(Long chatId, String texto, InlineKeyboardMarkup teclado) throws TelegramApiException {
        SendMessage mensaje = new SendMessage();
        mensaje.setChatId(chatId);
        mensaje.setText(texto);
        if (teclado != null) {
            mensaje.setReplyMarkup(teclado);
        }

        bot.execute(mensaje);
    }

    private void handleLogin(Long chatId, String password) throws TelegramApiException {
        try{
            String nombre = sesionesServicio.obtenerNombre(chatId);
            AuthResponse authResponse = backEndClient.login(nombre, password);
            sesionesServicio.guardarToken(chatId, authResponse.token());
            switch(authResponse.rol()){
                case "USUARIO":
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                    enviarMensaje(chatId, "Login exitoso! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                    break;
                case "ADMIN":
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                    enviarMensaje(chatId, "Login exitoso! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                    break;
                default:
                    break;
            }

        } catch (HttpClientErrorException e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                enviarMensaje(chatId, "Error al Iniciar Sesion: Contrseña Incorrecta. Intente nuevamente" , TecladoAuth.get());
            }
            enviarMensaje(chatId, "Error al Iniciar Sesion: "+ e.getResponseBodyAsString() + ". Intente Nuevamente" , TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Error al iniciar sesion", TecladoAuth.get());
        }
    }

    private void handleRegister(Long chatId, String password) throws TelegramApiException {
        try{
            String nombre = sesionesServicio.obtenerNombre(chatId);
            AuthResponse authResponse = backEndClient.register(nombre, password, RolesPersona.USUARIO);
            sesionesServicio.guardarToken(chatId, authResponse.token());
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Usuario registrado exitosomente! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e){
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Error al registrarse: "+ e.getResponseBodyAsString() + ". Intente Nuevamente" , TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Error al registrarse. Intente Nuevamente ", TecladoAuth.get());
        }

    }

    private void handleCrearCarta(Long chatId, String nombre) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearCarta(token, nombre, sesionesServicio.obtenerUltimoJuego(chatId));
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Carta creada exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se puede crear esa carta, pues la misma ya existe. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
        }
        catch (Exception e) {

        }
    }

    private void handleEliminarCarta(Long chatId, int cartaId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.eliminarCarta(token, cartaId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Carta eliminada exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro la carta. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
        }
        catch (Exception e) {

        }
    }

    private void handleCrearJuego(Long chatId, String nombre) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearJuego(token, nombre);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Juego creado exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se puede crear este juego, pues el mismo ya existe. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
        }
        catch (Exception e) {

        }
    }

    private void handleEliminarJuego(Long chatId, int juegoId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.eliminarJuego(token, juegoId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Juego eliminado exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
        }
        catch (Exception e) {

        }
    }

    private void handleCrearAdmin(Long chatId, String password) throws TelegramApiException {
        try{
            String nombre = sesionesServicio.obtenerNombreCreacionAdmin(chatId);
            AuthResponse authResponse = backEndClient.register(nombre, password, RolesPersona.ADMIN);
            sesionesServicio.guardarToken(chatId, authResponse.token());
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Nuevo admin creado exitosamente!", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "Ese nombre de usuario ya esta en uso. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
            }
        }
        catch (Exception e) {

        }

    }

    private void handleCrearPublicacion(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            CreacionPublicacion publicacion = sesionesServicio.obtenerCreacionPublicacion(chatId);
            CreacionPublicacionDTO publiDto = new CreacionPublicacionDTO(
                    publicacion.getCartaId(),
                    publicacion.getTitulo(),
                    publicacion.getDescripcion(),
                    publicacion.getIntereses(),
                    new ArrayList<>(),
                    publicacion.getPrecio(),
                    publicacion.getEstadoConservacion());
            backEndClient.crearPublicacion(token, publiDto);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Publicacion creada exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                if(e.getMessage().contains("juego")) {
                    enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                }
                if(e.getMessage().contains("carta")) {
                    enviarMensaje(chatId, "No se encontro la carta. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                }
            }
        }
        catch (Exception e) {}
    }

    private void handlePerfil(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.PERFIL);
            PersonaDTO personaInfo = backEndClient.getPersona(token);
            enviarMensaje(chatId, "Bienvenido: "+ personaInfo.nombre() + ". Eres un " + personaInfo.rol() + ". Que desea hacer?", TecladoPerfil.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void handlePublicacionesPerfil(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);

            List<PublicacionPersonaDTO> publicaciones = backEndClient.getPublicacionesPersona(token);
            if(publicaciones.isEmpty()) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.PERFIL);
                enviarMensaje(chatId, "No tienes publicaciones.", TecladoPerfil.get());
            }
            else{
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_PUBLICACION_PERFIL);
                for (PublicacionPersonaDTO publicacion : publicaciones) {
                    String text =
                            "Titulo: " + publicacion.titulo() + ".\n" +
                            "Carta: " + publicacion.tituloCarta() + ".\n" +
                            "Estado Carta: " + publicacion.estado() + ".\n" +
                            "Fecha Publicacion: " + publicacion.fechaPublicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n";

                    if(publicacion.fechaCierre() != null){
                        text += "Estado Publicación: Finalizada. \n" +
                                "Fecha de Cierre: " + publicacion.fechaCierre().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n";
                    }
                    else{
                        text += "Estado Publicacion: Abierta. \n";
                    }
                    enviarMensajeConFoto(chatId,text,publicacion.imagen(),TecladoVerPublicacion.get(publicacion.id()));
                }
                enviarMensaje(chatId, "Si desea ingresar a una de las publicaciones, toque el boton Ver Publicacion. Si no, toque el boton para volver al Menu Principal", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }


        }
        catch (Exception e) {
            enviarMensaje(chatId,e.getMessage());
        }
    }

    private void handleVerPublicacion(Long chatId, int idPubli) throws TelegramApiException{
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            RespuestaPublicacionDTO publicacion = backEndClient.getPublicacion(token, idPubli);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_PUBLICACION);

            for(String imagen : publicacion.imagenes()){
                if(!imagen.isEmpty()){
                    enviarMensajeConFoto(chatId,"",imagen,null);
                }
            }
            String texto_final =
                    "Titulo: " + publicacion.titulo() + ".\n" +
                    "Carta: " + publicacion.nombreCarta() + ".\n" +
                    "Estado carta: " + publicacion.estado() + ".\n" +
                    "Descripción: " + publicacion.descripcion() + ".\n" +
                    "Publicador: " + publicacion.nombrePublicador() + ".\n" +
                    "Fecha de publicación: " + publicacion.fechaPublicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n";
            if(publicacion.precio() > 0){
                texto_final += "Precio: " + publicacion.precio() + "\n";
            }
            if(!publicacion.intereses().isEmpty()){
                String intereses = String.join("\n", publicacion.intereses());
                texto_final += "Intereses: " + intereses + "\n";
            }
            if(publicacion.esPublicador()){
                enviarMensaje(chatId, texto_final, TecladoPublicacionPublicador.get(idPubli));
            }
            else{
                enviarMensaje(chatId, texto_final, TecladoPublicacionUsuario.get(idPubli));
            }


        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void handleOfertasRecibidasPerfil(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<OfertaPersonaDTO> ofertas = backEndClient.getOfertasRecibidas(token);
            if(ofertas.isEmpty()) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.PERFIL);
                enviarMensaje(chatId, "No recibiste ninguna oferta", TecladoPerfil.get());
            }
            else{
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_OFERTA_PERFIL);
                for (OfertaPersonaDTO oferta : ofertas) {
                    String text =
                            "Publicacion: " + oferta.tituloPublicacion() + ". \n" +
                            "Carta: " + oferta.nombreCarta() + ". \n" +
                            "Estado Oferta: " + oferta.estado() + ". \n" +
                            "Fecha de la oferta: " + oferta.fecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". \n" +
                            "Ofertante: " + oferta.nombreOfertante() + ".";

                    enviarMensajeConFoto(chatId,text,oferta.imagen(),TecladoVerOferta.get(oferta.id()));

                }
                enviarMensaje(chatId, "Si desea ingresar a una de las ofertas, toque el boton Ver Oferta. Si no, toque el boton para volver al Menu Principal", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void handleOfertasHechasPerfil(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<OfertaPersonaDTO> ofertas = backEndClient.getOfertasHechas(token);
            if(ofertas.isEmpty()) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.PERFIL);
                enviarMensaje(chatId, "No recibiste ninguna oferta", TecladoPerfil.get());
            }
            else{
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_OFERTA_PERFIL);
                for (OfertaPersonaDTO oferta : ofertas) {
                    String text =
                            "Publicacion: " + oferta.tituloPublicacion() + ". \n" +
                                    "Carta: " + oferta.nombreCarta() + ". \n" +
                                    "Estado Oferta: " + oferta.estado() + ". \n" +
                                    "Fecha de la oferta: " + oferta.fecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". \n";

                    enviarMensajeConFoto(chatId,text,oferta.imagen(),TecladoVerOferta.get(oferta.id()));

                }
                enviarMensaje(chatId, "Si desea ingresar a una de las ofertas, toque el boton Ver Oferta. Si no, toque el boton para volver al Menu Principal", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void handleVerOferta(Long chatId, int idOferta) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            RespuestaOfertaDTO oferta = backEndClient.getOferta(token, idOferta);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_OFERTA);
            String textoFinal =
                    "Titulo Publicacion: " + oferta.tituloPublicacion() + ".\n" +
                         "Fecha oferta: " + oferta.fecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n" +
                         "Estado de la Oferta: " + oferta.estadoOferta() + ".\n";
            if(oferta.valor() > 0){
                textoFinal += "Valor Ofrecido: " + oferta.valor() + "\n";
            }
            if(!oferta.cartas().isEmpty()){
                String intereses = String.join(", ", oferta.cartas());
                textoFinal += "Cartas Ofrecidas: " + intereses + "\n";
            }
            if(oferta.esPublicador()){
                textoFinal += "Nombre del Ofertante: " + oferta.nombreOfertante() + "\n";
                if(oferta.estadoOferta().equals("EN_ESPERA")){
                    enviarMensajeConFoto(chatId,textoFinal,oferta.imagen(),TecladoOfertaPublicador.get(idOferta));
                }
                else{
                    enviarMensajeConFoto(chatId,textoFinal,oferta.imagen(), TecladoVolverAlMenu.get(RolesPersona.USUARIO));
                }
            }
            else{
                textoFinal = textoFinal + "Nombre del Publicador: " + oferta.nombrePublicador() + "\n";
                enviarMensajeConFoto(chatId,textoFinal,oferta.imagen(), TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void handleCrearOferta(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearOferta(token, sesionesServicio.obtenerUltimaOferta(chatId));
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Oferta creada exitosamente! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Error al crear la oferta. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {

        }
    }

    private void handleBorrarPublicacion(Long chatId, int idPubli) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            backEndClient.borrarPublicacion(token, idPubli);
            enviarMensaje(chatId, "Publicación borrada exitosamente", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "No se pudo borrar la publicación", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "No se pudo borrar la publicación", TecladoMenuPrincipalUsuario.get());
        }

    }

    private void handleAceptarOferta(Long chatId, int idOferta) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.aceptarOferta(token, idOferta);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"Oferta Aceptada Exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"No se pudo aceptar la oferta.", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {

        }
    }

    private void handleRechazarOferta(Long chatId, int idOferta) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.rechazarOferta(token, idOferta);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"Oferta Rechazada Exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"No se pudo rechazar la oferta.", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {

        }
    }

    private void handlePublicaciones(Long chatId) throws TelegramApiException {
        handleVerPagina(chatId,1, new FiltrosPublicacion());
    }

    private void handleVerPagina(Long chatId, int pagina, FiltrosPublicacion filtros) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            PaginacionPublicacionDTO publicacionesPaginado = backEndClient.getPublicaciones(token,pagina, filtros);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_PUBLICACIONES);
            if(publicacionesPaginado.publicaciones().isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontraron publicaciones", TecladoMenuPrincipalUsuario.get());
            }
            else{

                for(PublicacionReducidaDTO publicacion : publicacionesPaginado.publicaciones()){
                    String textoFinal = "Titulo: " + publicacion.titulo() + ". \n" +
                            "Carta: " + publicacion.tituloCarta() + ". \n" +
                            "Estado Carta: " + publicacion.estado() + ".";

                    enviarMensajeConFoto(chatId,textoFinal,publicacion.imagen(),TecladoVerPublicacion.get(publicacion.id()));
                }
                enviarMensaje(chatId, "Interactue con los botones para ver mas publicaciones, aplicar filtros o volver al Menu Principal", TecladoViendoPublicaciones.get(publicacionesPaginado.pagAnterior(),publicacionesPaginado.pagSiguiente()));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarJuegos(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_JUEGO);
            if(juegos.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(JuegoDTO juego : juegos){
                    String texto = juego.id() + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoFiltro.get(juego.id(), juego.nombre()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarJuegosCreacionCarta(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_JUEGO_CREACION_CARTA);
            if(juegos.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(JuegoDTO juego : juegos){
                    String texto = juego.id() + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoCreacionCarta.get(juego.id(), juego.nombre()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarJuegosEliminacion(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_JUEGO_ELIMINACION);
            if(juegos.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(JuegoDTO juego : juegos){
                    String texto = juego.id() + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoEliminacion.get(juego.id(), juego.nombre()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarJuegosCreacionPublicacion(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_JUEGO_PUBLICACION);
            if(juegos.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(JuegoDTO juego : juegos){
                    String texto = juego.id() + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoCreacionPublicacion.get(juego.id(), juego.nombre()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarCartasCreacionPublicacion(Long chatId, int juegoId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<CartaDTO> cartas = backEndClient.getCartasByJuego(token,juegoId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_CARTA_PUBLICACION);
            if(cartas.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(CartaDTO carta : cartas){
                    String texto = carta.cartaId() + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaCreacionPublicacion.get(carta.cartaId(), carta.nombreCarta()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {}
    }

    private void mostrarCartasFiltro(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            FiltrosPublicacion filtro = sesionesServicio.obtenerFiltros(chatId);
            List<CartaDTO> cartas;
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_CARTA);
            if(filtro.getJuegoId() == null){
                cartas = backEndClient.getCartas(token);
            }
            else{
                cartas = backEndClient.getCartasByJuego(token,filtro.getJuegoId());
            }
            if(cartas.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(CartaDTO carta : cartas){
                    String texto = carta.cartaId() + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaFiltro.get(carta.cartaId(), carta.nombreCarta()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {}
    }

    private void mostrarCartasEliminacion(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<CartaDTO> cartas = backEndClient.getCartas(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_CARTA_ELIMINACION);
            if(cartas.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(CartaDTO carta : cartas){
                    String texto = carta.cartaId() + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaEliminacion.get(carta.cartaId(), carta.nombreCarta()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));

            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {}
    }

    private void mostrarCartas(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<CartaDTO> cartas = backEndClient.getCartas(token);;

            if(cartas.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                String texto = "";
                for(CartaDTO carta : cartas){
                    texto += carta.cartaId() + ". " + carta.nombreCarta() + ". Juego: "+ carta.nombreJuego()+ ". \n";

                }
                enviarMensaje(chatId, texto);
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {}
    }

    private void mostrarEstadosFiltros(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<String> estados = backEndClient.getEstados(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_ESTADO);
            if(estados.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Estado", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(int i = 0; i<estados.size(); i++){
                    String texto = i + ". " + estados.get(i) + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionEstadoFiltro.get(estados.get(i)));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }

    private void mostrarEstadosPublicacion(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<String> estados = backEndClient.getEstados(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_ESTADO_PUBLICACION);
            if(estados.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Estado", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(int i = 0; i<estados.size(); i++){
                    String texto = i + ". " + estados.get(i) + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionEstadoPublicacion.get(estados.get(i)));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {

        }
        catch (Exception e) {

        }
    }




}

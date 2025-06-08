package bot.Service;

import bot.*;
import bot.DTO.*;
import bot.Teclados.*;
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


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * Maneja los mensajes de texto recibidos por el bot según el estado actual de la conversación del usuario.
     * <p>
     * Este método actúa como un enrutador principal para interpretar el texto enviado por el usuario,
     * respondiendo de manera diferente dependiendo del estado de la conversación previamente guardado en la sesión.
     * Se encarga de gestionar procesos como login, registro, creación de publicaciones, ofertas y filtros.
     *
     * <p>
     * Para cada estado posible del BotState, el método ejecuta las acciones correspondientes como:
     * guardar datos en la sesión, cambiar el estado, invocar otras funciones de manejo (como `handleLogin`, `handleCrearOferta`, etc.),
     * o enviar respuestas directas mediante el método `enviarMensaje`.
     * </p>
     *
     * <p>
     * Además, valida formatos (por ejemplo, fechas y números), verifica permisos con el backend (incluyendo detección de sesión vencida)
     * y guía al usuario a través de los distintos pasos de interacción.
     * </p>
     *
     * @param update El objeto Update recibido desde Telegram, que contiene el mensaje enviado por el usuario.
     * @throws TelegramApiException Si ocurre un error al enviar respuestas mediante el bot de Telegram.
     */

    public void handleMensaje(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        BotState estado = sesionesServicio.obtenerEstadoConversacion(chatId);

        switch (estado) {
            case ESPERANDO_NOMBRE_LOGIN:
                if(text.length()<31){
                    sesionesServicio.guardarNombre(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_LOGIN);
                    enviarMensaje(chatId, "Ingrese su contraseña:");
                }
                else{
                    enviarMensaje(chatId, "Error, los nombre de usuarios solo pueden tener hasta 20 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_CONTRASENIA_LOGIN:
                if(text.length()<51){
                    handleLogin(chatId, text);
                }
                else{
                    enviarMensaje(chatId, "Error, las contraseñas solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_NOMBRE_REGISTER:
                if(text.length()<31){
                    sesionesServicio.guardarNombre(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_REGISTER);
                    enviarMensaje(chatId, "Ingrese su contraseña:");
                }
                else{
                    enviarMensaje(chatId, "Error, los nombre de usuarios solo pueden tener hasta 20 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_CONTRASENIA_REGISTER:
                if(text.length()<51){
                    handleRegister(chatId, text);
                }
                else{
                    enviarMensaje(chatId, "Error, las contraseñas solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

                break;
            case INICIO:
                sesionesServicio.limpiar(chatId);
                handleInicio(chatId);
                break;

            case ESPERANDO_NOMBRE_CREACION_CARTA:
                if(text.length()<101){
                    handleCrearCarta(chatId, text);
                }
                else{
                    enviarMensaje(chatId, "Error, los nombres de las cartas solo pueden tener hasta 100 caracteres. Pruebe nuevamente");
                }
                break;

            case ESPERANDO_NOMBRE_CREACION_JUEGO:
                if(text.length()<51){
                    handleCrearJuego(chatId, text);
                }
                else{
                    enviarMensaje(chatId, "Error, los nombres de los juegos solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_NOMBRE_CREACION_ADMIN:
                if(text.length()<31){
                    sesionesServicio.guardarNombreCreacionAdmin(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONTRASENIA_CREACION_ADMIN);
                    enviarMensaje(chatId, "Ingrese la contraseña del nuevo admin");
                }
                else{
                    enviarMensaje(chatId, "Error, los nombre de usuarios solo pueden tener hasta 20 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_CONTRASENIA_CREACION_ADMIN:
                if(text.length()<51){
                    handleCrearAdmin(chatId, text);
                }
                else{
                    enviarMensaje(chatId, "Error, las contraseñas solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_TITULO_PUBLICACION:
                if(text.length()<51){
                    sesionesServicio.guardarTituloPublicacion(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_DESCRIPCION_PUBLICACION);
                    enviarMensaje(chatId, "Ingrese la descripcion de la publicacion");
                }
                else{
                    enviarMensaje(chatId, "Error, los titulos solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_DESCRIPCION_PUBLICACION:
                if(text.length()<501){
                    sesionesServicio.guardarDescripcionPublicacion(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_VALOR_PUBLICACION);
                    enviarMensaje(chatId, "Desea ingresar un valor estimado de la carta?", TecladoCreacionPublicacion.get("VALOR"));
                }
                else{
                    enviarMensaje(chatId, "Error, las descripciones solo pueden tener hasta 500 caracteres. Pruebe nuevamente");
                }

                break;

            case ESPERANDO_VALOR_PUBLICACION:
                if (!text.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                    break;
                }
                try{
                    float valorEstimado = Float.parseFloat(text);
                    if(valorEstimado > 999999999.99d || valorEstimado < 1d){
                        enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y  el maximo es 999.999.999,99 y solo puede tener 2 valores decimales");
                        break;
                    }
                    sesionesServicio.guardarValorPublicacion(chatId, valorEstimado);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_INTERESES_PUBLICACION);
                    enviarMensaje(chatId, "Desea ingresar las cartas que le interesan a cambio de esta carta?", TecladoCreacionPublicacion.get("INTERESES"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999.999.999,99 y solo puede tener 2 valores decimales");
                }
                break;

            case ESPERANDO_INTERESES_PUBLICACION:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> interesesNum = new ArrayList<>();
                    for (String part : parts) {
                        interesesNum.add(Integer.parseInt(part));
                    }
                    List<CartaNumId> listaCartasNum = sesionesServicio.obtenerCartas(chatId);
                    if(new HashSet<>(listaCartasNum.stream().map(CartaNumId::getNumAsociado).toList()).containsAll(interesesNum)){
                        sesionesServicio.guardarInteresesPublicacion(chatId, listaCartasNum.stream().filter(cartaNumId -> interesesNum.contains(cartaNumId.getNumAsociado())).map(CartaNumId::getId).toList());
                        sesionesServicio.limpiarListaCartas(chatId);
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
                    if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch (Exception e){
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                break;

            case ESPERANDO_SOLO_CARTAS_OFERTA:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> cartasNum = new ArrayList<>();
                    for (String part : parts) {
                        cartasNum.add(Integer.parseInt(part));
                    }
                    List<CartaNumId> listaCartasNum = sesionesServicio.obtenerCartas(chatId);
                    if(new HashSet<>(listaCartasNum.stream().map(CartaNumId::getNumAsociado).toList()).containsAll(cartasNum)){
                        sesionesServicio.guardarCartasOferta(chatId, listaCartasNum.stream().filter(cartaNumId -> cartasNum.contains(cartaNumId.getNumAsociado())).map(CartaNumId::getId).toList());
                        sesionesServicio.guardarValorUltimaOferta(chatId,-1f);
                        sesionesServicio.limpiarListaCartas(chatId);
                        handleCrearOferta(chatId);
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }


                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }
                catch (HttpClientErrorException e){
                    if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch (Exception e){
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                break;

            case ESPERANDO_SOLO_DINERO_OFERTA:
                if (!text.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                    break;
                }
                try{
                    float precioOfertado = Float.parseFloat(text);
                    if(precioOfertado > 999999999.99d || precioOfertado < 1d){
                        enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde el minimo es 1 y que el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                        break;
                    }
                    sesionesServicio.guardarValorUltimaOferta(chatId, precioOfertado);
                    sesionesServicio.guardarCartasOferta(chatId, new ArrayList<>());
                    handleCrearOferta(chatId);

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                }
                break;

            case ESPERANDO_CARTAS_OFERTA:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> cartasNum = new ArrayList<>();
                    for (String part : parts) {
                        cartasNum.add(Integer.parseInt(part));
                    }
                    List<CartaNumId> listaCartasNum = sesionesServicio.obtenerCartas(chatId);
                    if(new HashSet<>(listaCartasNum.stream().map(CartaNumId::getNumAsociado).toList()).containsAll(cartasNum)){
                        sesionesServicio.guardarCartasOferta(chatId, listaCartasNum.stream().filter(cartaNumId -> cartasNum.contains(cartaNumId.getNumAsociado())).map(CartaNumId::getId).toList());
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_DINERO_OFERTA);
                        sesionesServicio.limpiarListaCartas(chatId);
                        enviarMensaje(chatId,"Ingrese el valor que quiere ofrecer, utilice puntos para las unidades decimales (Minimo 1 - Maximo 999999999.99)");
                    }
                    else{
                        enviarMensaje(chatId, "Por favor ingrese solo numeros de la lista de cartas");
                    }

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese todos los numeros en el formato correcto, separados por espacios");
                }

                catch (HttpClientErrorException e){
                    if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch (Exception e){
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                break;

            case ESPERANDO_DINERO_OFERTA:
                if (!text.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                    break;
                }
                try{
                    float precioOfertado = Float.parseFloat(text);
                    if(precioOfertado > 999999999.99d || precioOfertado < 1d){
                        enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                        break;
                    }
                    sesionesServicio.guardarValorUltimaOferta(chatId, precioOfertado);
                    handleCrearOferta(chatId);

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                }
                break;

            case ESPERANDO_FILTRO_NOMBRE:
                if(text.length()<51){
                    sesionesServicio.guardarNombreFiltro(chatId, text);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_CONFIRMACION_FILTRO_ESTADO);
                    enviarMensaje(chatId, "Desea aplicar el filtro de estado conservacion de la carta?", TecladoFiltro.get("ESTADO"));
                }
                else{
                    enviarMensaje(chatId, "Error, los titulos solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }

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
                if (!text.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                    break;
                }
                try{
                    float precioInicial = Float.parseFloat(text);
                    if(precioInicial > 999999999.99d || precioInicial < 1d){
                        enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                        break;
                    }
                    sesionesServicio.guardarPrecioInicial(chatId, precioInicial);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_PRECIO_FINAL);
                    enviarMensaje(chatId, "Desea aplicar el filtro de precio maximo?", TecladoFiltro.get("PRECIO_FINAL"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                }
                break;
            case ESPERANDO_FILTRO_PRECIO_FINAL:
                if (!text.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                    break;
                }
                try{
                    float precioFinal = Float.parseFloat(text);
                    if(precioFinal > 999999999.99d || precioFinal < 1d){
                        enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                        break;
                    }
                    sesionesServicio.guardarPrecioFinal(chatId, precioFinal);
                    sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_CONFIRMACION_FILTRO_INTERESES);
                    enviarMensaje(chatId, "Desea aplicar el filtro de intereses de cartas?", TecladoFiltro.get("INTERESES"));

                }
                catch (NumberFormatException e){
                    enviarMensaje(chatId, "Por favor ingrese el precio en un formato correcto. Recuerde que el minimo es 1 y el maximo es 999999999.99 y solo puede tener 2 valores decimales");
                }
                break;
            case ESPERANDO_FILTRO_INTERESES:
                try {
                    String [] parts = text.split("\\s+");
                    List<Integer> interesesNum = new ArrayList<>();
                    for (String part : parts) {
                        interesesNum.add(Integer.parseInt(part));
                    }
                    List<CartaNumId> listaCartasNum = sesionesServicio.obtenerCartas(chatId);
                    if(new HashSet<>(listaCartasNum.stream().map(CartaNumId::getNumAsociado).toList()).containsAll(interesesNum)){
                        sesionesServicio.guardarIntereses(chatId, listaCartasNum.stream().filter(cartaNumId -> interesesNum.contains(cartaNumId.getNumAsociado())).map(CartaNumId::getId).toList());
                        sesionesServicio.limpiarListaCartas(chatId);
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
                    if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch (Exception e){
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                break;

            case ESPERANDO_NOMBRE_JUEGO_CREACION_SOLICITUD_JUEGO:
                if(text.length() < 51){
                    try{
                        backEndClient.crearSolicitudJuego(sesionesServicio.obtenerToken(chatId), text);
                        sesionesServicio.limpiarOferta(chatId);
                        sesionesServicio.limpiarFiltros(chatId);
                        sesionesServicio.limpiarPublicacion(chatId);
                        sesionesServicio.limpiarListaCartas(chatId);
                        sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                        enviarMensaje(chatId, "Solicitud creada Exitosamente! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                    }
                    catch (HttpClientErrorException e){
                        if(e.getStatusCode() == HttpStatus.CONFLICT) {
                            enviarMensaje(chatId, "Ya existe un juego con ese nombre", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
                        }
                        if(e.getStatusCode() == HttpStatus.PAYLOAD_TOO_LARGE) {
                            enviarMensaje(chatId, "Error, los juegos solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                        }
                        if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                            sesionesServicio.limpiarOferta(chatId);
                            sesionesServicio.limpiarFiltros(chatId);
                            sesionesServicio.limpiarPublicacion(chatId);
                            sesionesServicio.limpiarListaCartas(chatId);
                            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                            enviarMensaje(chatId, "Se ha alcanzado el limite diario de solicitudes de creacion de Juego", TecladoMenuPrincipalUsuario.get());
                        }
                    }
                }
                else{
                    enviarMensaje(chatId, "Error, los juegos solo pueden tener hasta 50 caracteres. Pruebe nuevamente");
                }
                break;

            case ESPERANDO_NOMBRE_CARTA_SOLICTUD_CARTA:
                if(text.length() < 101){
                    try{
                        String juegoId = sesionesServicio.obtenerIdJuegoSolicitudCarta(chatId);
                        backEndClient.crearSolicitudCarta(sesionesServicio.obtenerToken(chatId), text, juegoId);
                        sesionesServicio.limpiarOferta(chatId);
                        sesionesServicio.limpiarFiltros(chatId);
                        sesionesServicio.limpiarPublicacion(chatId);
                        sesionesServicio.limpiarListaCartas(chatId);
                        sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                        enviarMensaje(chatId, "Solicitud creada Exitosamente! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                    }
                    catch (HttpClientErrorException e){
                        if(e.getStatusCode() == HttpStatus.CONFLICT) {
                            enviarMensaje(chatId, "Ya existe una carta con ese nombre", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
                            return;
                        }
                        if(e.getStatusCode() == HttpStatus.PAYLOAD_TOO_LARGE) {
                            enviarMensaje(chatId, "Error, los juegos solo pueden tener hasta 100 caracteres. Pruebe nuevamente");
                            return;
                        }
                        if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                            sesionesServicio.limpiarOferta(chatId);
                            sesionesServicio.limpiarFiltros(chatId);
                            sesionesServicio.limpiarPublicacion(chatId);
                            sesionesServicio.limpiarListaCartas(chatId);
                            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                            enviarMensaje(chatId, "Se ha alcanzado el limite diario de solicitudes de creacion de Carta", TecladoMenuPrincipalUsuario.get());
                            return;
                        }
                        if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            sesionesServicio.limpiarOferta(chatId);
                            sesionesServicio.limpiarFiltros(chatId);
                            sesionesServicio.limpiarPublicacion(chatId);
                            sesionesServicio.limpiarListaCartas(chatId);
                            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                            enviarMensaje(chatId, "No se encontro el juego", TecladoMenuPrincipalUsuario.get());
                            return;
                        }
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                    }
                    catch (Exception e){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                    }
                }
                else{
                    enviarMensaje(chatId, "Error, las cartas solo pueden tener hasta 100 caracteres. Pruebe nuevamente");
                }
                break;
            default:
                enviarMensaje(chatId, "No entiendo lo que estas diciendo. Si hay botones, usalos");
                break;

        }
    }

    /**
     * Maneja las interacciones del usuario con botones del teclado inline (callback data).
     * Este método interpreta el dato del callback (callbackQuery.getData()) y actúa en función
     * del estado actual del usuario (almacenado en sesionesServicio), permitiendo:
     *
     * Crear cartas y publicaciones.
     * Eliminar cartas, juegos o publicaciones.
     * Visualizar publicaciones u ofertas.
     * Filtrar publicaciones.
     * Manejar login, registro, perfil, cierre de sesión, etc.
     *
     *
     * Cada acción es controlada por el estado de conversación del usuario (BotState),
     * y si no corresponde con el esperado para la acción, se le notifica que no puede realizarla.
     *
     * @param update el objeto {@link Update} recibido por el bot, que contiene el callbackQuery con la acción del usuario.
     * @throws TelegramApiException si ocurre un error al enviar un mensaje de respuesta mediante la API de Telegram.
     */
    public void handleCallBack(Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        if(data.contains("CREACION_CARTA_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.ESPERANDO_JUEGO_CREACION_CARTA){
                String [] parts = data.split("_");
                String juegoId = parts[3];
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
                String cartaId = parts[2];
                handleEliminarCarta(chatId,cartaId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ELIMINAR_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId)==BotState.ESPERANDO_SELECCION_JUEGO_ELIMINACION){
                String [] parts = data.split("_");
                String juegoId = parts[2];
                handleEliminarJuego(chatId,juegoId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("CREACION_PUBLICACION_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_JUEGO_PUBLICACION){
                String [] parts = data.split("_");
                String juegoId = parts[3];
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
                String cartaId = parts[2];
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
                String idPublicacion = parts[2];
                handleVerPublicacion(chatId,idPublicacion);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;

        }
        if(data.contains("VER_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_OFERTA_PERFIL){
                String [] parts = data.split("_");
                String idOferta= parts[2];
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
                String publicacionId = parts[2];
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
                String idOferta= parts[2];
                handleAceptarOferta(chatId,idOferta);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("RECHAZAR_OFERTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_OFERTA){
                String [] parts = data.split("_");
                String idOferta= parts[2];
                handleRechazarOferta(chatId,idOferta);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("BORRAR_PUBLICACION_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_PUBLICACION) {
                String [] parts = data.split("_");
                String idPubli= parts[2];
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
                String juegoId = parts[2];
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
                String cartaId = parts[2];
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
        if(data.contains("JUEGO_CREACION_SOLICIUTD_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.ESPERANDO_SELECCION_JUEGO_CREACION_SOLICITUD_CARTA) {
                String [] parts = data.split("_");
                String juegoId = parts[4];
                sesionesServicio.guardarIdJuegoSolicitudCarta(chatId, juegoId);
                sesionesServicio.guardarEstadoConversacion(chatId,BotState.ESPERANDO_NOMBRE_CARTA_SOLICTUD_CARTA);
                enviarMensaje(chatId, "Ingrese el nombre de la carta que quiere crear (Maximo 100 caracteres)");
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("VER_SOLICITUD_JUEGO_UNICA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_TODAS_SOLICITUDES_JUEGO || sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUDES_EN_ESPERA_JUEGO){
                String [] parts = data.split("_");
                String soliId = parts[4];
                handleVerSolicitudJuego(chatId, soliId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ACEPTAR_SOLICITUD_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUD_JUEGO){
                String [] parts = data.split("_");
                String soliId = parts[3];
                try {
                    backEndClient.administrarSolicitudJuego(sesionesServicio.obtenerToken(chatId), "APROBADA", soliId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                    enviarMensaje(chatId, "Solicitud aprobada con exito!", TecladoMenuPrincipalAdmin.get());
                }
                catch (HttpClientErrorException ex){
                    if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.FORBIDDEN){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "No se encontro la solicitud, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.CONFLICT){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "Error al aceptar, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch(Exception e) {
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                }
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("RECHAZAR_SOLICITUD_JUEGO_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUD_JUEGO){
                String [] parts = data.split("_");
                String soliId = parts[3];
                try {
                    backEndClient.administrarSolicitudJuego(sesionesServicio.obtenerToken(chatId), "RECHAZADA", soliId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                    enviarMensaje(chatId, "Solicitud rechazada con exito!", TecladoMenuPrincipalAdmin.get());
                }
                catch (HttpClientErrorException ex){
                    if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.FORBIDDEN){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "No se encontro la solicitud, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.CONFLICT){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "Error al rechazar, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch(Exception e) {
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                }
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("VER_SOLICITUD_CARTA_UNICA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_TODAS_SOLICITUDES_CARTA || sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUDES_EN_ESPERA_CARTA){
                String [] parts = data.split("_");
                String soliId = parts[4];
                handleVerSolicitudCarta(chatId, soliId);
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("ACEPTAR_SOLICITUD_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUD_CARTA){
                String [] parts = data.split("_");
                String soliId = parts[3];
                try {
                    backEndClient.administrarSolicitudCarta(sesionesServicio.obtenerToken(chatId), "APROBADA", soliId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                    enviarMensaje(chatId, "Solicitud aprobada con exito!", TecladoMenuPrincipalAdmin.get());
                }
                catch (HttpClientErrorException ex){
                    if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.FORBIDDEN){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "No se encontro la solicitud, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    if(ex.getStatusCode() == HttpStatus.CONFLICT){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "Error al aceptar, la carta ya existia, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                        return;
                    }
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
                }
                catch(Exception e) {
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                }
                return;
            }
            enviarMensaje(chatId, "No podes hacer esto ahora mismo");
            return;
        }
        if(data.contains("RECHAZAR_SOLICITUD_CARTA_")){
            if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_SOLICITUD_CARTA){
                String [] parts = data.split("_");
                String soliId = parts[3];
                try {
                    backEndClient.administrarSolicitudCarta(sesionesServicio.obtenerToken(chatId), "RECHAZADA", soliId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                    enviarMensaje(chatId, "Solicitud rechazada con exito!", TecladoMenuPrincipalAdmin.get());
                }
                catch (HttpClientErrorException ex){
                    if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                    }
                    if(ex.getStatusCode() == HttpStatus.FORBIDDEN){
                        sesionesServicio.limpiar(chatId);
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                        enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                    }
                    if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "No se encontro la solicitud, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                    }
                    if(ex.getStatusCode() == HttpStatus.CONFLICT){
                        sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                        enviarMensaje(chatId, "Error al rechazar, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                    }
                }
                catch(Exception e) {
                    sesionesServicio.limpiar(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                    enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                }
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
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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
                    enviarMensaje(chatId, "Ingrese el valor que quiere ofrecer, utilice puntos para las unidades decimales (Minimo 1 - Maximo 999999999.99)");
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
                    enviarMensaje(chatId, "Ingrese el valor estimado de la carta, utilice puntos para las unidades decimales (Minimo 1 - Maximo 999999999.99)");
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
                    mostrarJuegosFiltro(chatId);
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
                    enviarMensaje(chatId, "Ingrese el precio minimo, utilice puntos para las unidades decimales (Minimo 1 - Maximo 999999999.99)");
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
                    enviarMensaje(chatId, "Ingrese el precio maximo, utilice puntos para las unidades decimales (Minimo 1 - Maximo 999999999.99)");
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

            case "CREAR_SOLICITUD_JUEGO":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_NOMBRE_JUEGO_CREACION_SOLICITUD_JUEGO);
                    enviarMensaje(chatId, "Ingrese el nombre del juego que quiere crear (Maximo 50 caracteres)", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "CREAR_SOLICITUD_CARTA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL){
                    sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_JUEGO_CREACION_SOLICITUD_CARTA);
                    enviarMensaje(chatId, "Seleccione el juego para el que desea crear una carta", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
                    mostrarJuegosCreacionSolicitudCarta(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SOLICITUDES_CARTAS_PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.PERFIL){
                    mostrarSolictudesCartaPerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "SOLICITUDES_JUEGOS_PERFIL":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.PERFIL){
                    mostrarSolictudesJuegoPerfil(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_SOLICITUDES_JUEGOS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_MENU_SOLICITUDES_JUEGO_ADMIN);
                    enviarMensaje(chatId, "Que desea hacer?", TecladoVerSolicitudesJuegos.get());
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_SOLICITUDES_CARTAS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.MENU_PRINCIPAL_ADMIN){
                    sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_MENU_SOLICITUDES_CARTA_ADMIN);
                    enviarMensaje(chatId, "Que desea hacer?", TecladoVerSolictudesCartas.get());
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_TODAS_SOLICITUDES_JUEGOS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_MENU_SOLICITUDES_JUEGO_ADMIN){
                    mostrarTodasSolicitudesJuego(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_SOLICITUDES_JUEGOS_EN_ESPERA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_MENU_SOLICITUDES_JUEGO_ADMIN){
                    mostrarSolicitudesEnEsperaJuego(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_TODAS_SOLICITUDES_CARTAS":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_MENU_SOLICITUDES_CARTA_ADMIN){
                    mostrarTodasSolicitudesCarta(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;

            case "VER_SOLICITUDES_CARTAS_EN_ESPERA":
                if(sesionesServicio.obtenerEstadoConversacion(chatId) == BotState.VIENDO_MENU_SOLICITUDES_CARTA_ADMIN){
                    mostrarSolicitudesEnEsperaCarta(chatId);
                    break;
                }
                enviarMensaje(chatId, "No podes hacer esto ahora mismo");
                break;
            default:
                handleInicio(chatId);

                break;
        }

    }

    /**
     * Envía un mensaje de texto al usuario, opcionalmente acompañado de un teclado inline.
     * <p>
     * Este método construye y envía un mensaje de texto al usuario identificado por chatId.
     * Si se proporciona un teclado, se adjunta al mensaje como respuesta interactiva.
     *
     * @param chatId  El identificador del chat de Telegram del usuario.
     * @param texto   El contenido del mensaje de texto a enviar.
     * @param teclado El teclado inline a incluir junto al mensaje, o null si no se desea mostrar uno.
     * @throws TelegramApiException Si ocurre un error al enviar el mensaje mediante el bot de Telegram.
     */
    private void enviarMensaje(Long chatId, String texto, InlineKeyboardMarkup teclado) throws TelegramApiException {
        SendMessage mensaje = new SendMessage();
        mensaje.setChatId(chatId);
        mensaje.setText(texto);
        if (teclado != null) {
            mensaje.setReplyMarkup(teclado);
        }

        bot.execute(mensaje);
    }

    /**
     * Envía un mensaje de texto simple al usuario sin incluir ningún teclado adicional.
     * <p>
     *
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param text   El contenido del mensaje de texto a enviar.
     * @throws TelegramApiException Si ocurre un error al enviar el mensaje mediante el bot de Telegram.
     */
    private void enviarMensaje(Long chatId, String text) throws TelegramApiException {
        enviarMensaje(chatId, text, null);
    }

    /**
     * Envía un mensaje con una imagen al usuario, opcionalmente acompañado de texto y un teclado inline.
     * <p>
     * Si se proporciona una imagen válida (no vacía), se envía como una foto.
     * Si ocurre algún error al enviar la imagen, o si la imagen está vacía, se envía únicamente el texto
     * como mensaje normal.
     * <p>
     * El teclado inline también se incluye si se proporciona.
     *
     * @param chatId  El identificador del chat de Telegram del usuario.
     * @param text    El texto a mostrar como leyenda de la imagen o como mensaje alternativo si no hay imagen.
     * @param imagen  La URL de la imagen a enviar.
     * @param teclado El teclado inline a incluir junto al mensaje, o null si no se desea mostrar uno.
     * @throws TelegramApiException Si ocurre un error al enviar el mensaje mediante el bot de Telegram.
     */
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


    /**
     * Maneja el estado inicial del usuario cuando comienza a interactuar con el bot.
     * <p>
     * Esta función establece el estado de conversación del usuario en INICIO
     * y le muestra el teclado con las opciones de autenticación disponibles, como
     * iniciar sesión o registrarse.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje mediante el bot de Telegram.
     */
    private void handleInicio(Long chatId) throws TelegramApiException {
        sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
        enviarMensaje(chatId, "Que desea hacer?", TecladoAuth.get());
    }

    /**
     * Maneja el proceso de inicio de sesión de un usuario utilizando el nombre previamente guardado
     * en la sesión y la contraseña proporcionada.
     * <p>
     * Esta función intenta autenticar al usuario en el backend usando el nombre recuperado de la sesión
     * y la contraseña ingresada. Si el login es exitoso, se guarda el token JWT devuelto, se actualiza
     * el estado de conversación según el rol del usuario (USUARIO o ADMIN) y se muestra el menú correspondiente.
     * <p>
     * En caso de errores, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: Contraseña incorrecta. Se informa al usuario y se vuelve al menú de autenticación.
     * Otro HttpClientErrorException: Se muestra el mensaje de error proporcionado por el backend y se retorna al menú de autenticación.
     * Exception: Para cualquier otro error inesperado, se informa al usuario que ocurrió un error genérico y se vuelve al menú de autenticación.
     *
     *
     * @param chatId   El identificador del chat de Telegram del usuario.
     * @param password La contraseña ingresada por el usuario para iniciar sesión.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje mediante el bot de Telegram.
     */
    private void handleLogin(Long chatId, String password) throws TelegramApiException {
        try{
            String nombre = sesionesServicio.obtenerNombre(chatId);
            AuthResponse authResponse = backEndClient.login(nombre, password);
            sesionesServicio.guardarToken(chatId, authResponse.token());
            switch(authResponse.rol()){
                case "USUARIO":
                    sesionesServicio.limpiarOferta(chatId);
                    sesionesServicio.limpiarFiltros(chatId);
                    sesionesServicio.limpiarPublicacion(chatId);
                    sesionesServicio.limpiarListaCartas(chatId);
                    sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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
            else{
                enviarMensaje(chatId, "Error al Iniciar Sesion: "+ e.getResponseBodyAsString() + ". Intente Nuevamente" , TecladoAuth.get());
            }

        }
        catch (Exception e) {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Error al iniciar sesion", TecladoAuth.get());
        }
    }

    /**
     * Maneja el proceso de registro de un nuevo usuario en el sistema.
     * <p>
     * Esta función recupera el nombre previamente guardado en sesión para el chatId,
     * y luego intenta registrar al usuario en el backend utilizando ese nombre, la contraseña
     * proporcionada y un rol predeterminado de USUARIO. Si el registro es exitoso,
     * se guarda el token JWT recibido, se actualiza el estado de conversación y se muestra el
     * menú principal para usuarios.
     * <p>
     * En caso de error en la solicitud HTTP (por ejemplo, usuario ya existente o datos inválidos),
     * se informa al usuario y se lo devuelve al estado inicial. Lo mismo ocurre si hay una excepción
     * inesperada.
     *
     * @param chatId   El identificador del chat de Telegram del usuario.
     * @param password La contraseña ingresada por el usuario para registrarse.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje mediante el bot de Telegram.
     */
    private void handleRegister(Long chatId, String password) throws TelegramApiException {
        try{
            String nombre = sesionesServicio.obtenerNombre(chatId);
            AuthResponse authResponse = backEndClient.register(nombre, password, RolesPersona.USUARIO);
            sesionesServicio.guardarToken(chatId, authResponse.token());
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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

    /**
     * Maneja la creación de una nueva carta para el juego actual asociado al chat.
     * <p>
     * Esta función intenta crear una carta en el backend usando el nombre proporcionado
     * y el identificador del juego asociado al chatId. Luego actualiza el estado
     * de conversación del usuario y envía un mensaje con el resultado de la operación.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 403 FORBIDDEN: El usuario no tiene permisos. Se limpia la sesión y se muestra el menú de autenticación.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     * 409 CONFLICT: Ya existe una carta con ese nombre. Se informa al usuario y se vuelve al menú de admin.
     * 404 NOT FOUND: No se encuentra el juego asociado. Se informa al usuario y se vuelve al menú de admin.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param nombre El nombre de la carta a crear.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleCrearCarta(Long chatId, String nombre) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearCarta(token, nombre, sesionesServicio.obtenerUltimoJuego(chatId));
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Carta creada exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se puede crear esa carta, pues la misma ya existe. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la eliminación de una carta del juego asociado al chat.
     * <p>
     * Esta función intenta eliminar una carta en el backend usando el identificador de la carta
     * y el token de autenticación del usuario. Luego, dependiendo del resultado de la operación,
     * actualiza el estado de la conversación del usuario y envía un mensaje con el resultado.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 403 FORBIDDEN: El usuario no tiene permisos para realizar la acción. Se limpia la sesión y se muestra el menú de autenticación.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     * 404 NOT FOUND: No se encuentra la carta a eliminar. Se informa al usuario y se vuelve al menú de administrador.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param cartaId El identificador de la carta que se desea eliminar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleEliminarCarta(Long chatId, String cartaId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.eliminarCarta(token, cartaId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Carta eliminada exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro la carta. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la creación de un nuevo juego para el sistema asociado al chat.
     * <p>
     * Esta función intenta crear un juego en el backend usando el nombre proporcionado
     * y el token de autenticación del usuario. Luego, dependiendo del resultado de la operación,
     * actualiza el estado de la conversación del usuario y envía un mensaje con el resultado.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 403 FORBIDDEN: El usuario no tiene permisos para realizar la acción. Se limpia la sesión y se muestra el menú de autenticación.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     * 409 CONFLICT: Ya existe un juego con ese nombre. Se informa al usuario y se vuelve al menú de admin.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param nombre El nombre del juego a crear.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleCrearJuego(Long chatId, String nombre) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearJuego(token, nombre);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Juego creado exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se puede crear este juego, pues el mismo ya existe. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la eliminación de un juego del sistema asociado al chat.
     * <p>
     * Esta función intenta eliminar un juego en el backend usando el identificador del juego proporcionado
     * y el token de autenticación del usuario. Luego, dependiendo del resultado de la operación,
     * actualiza el estado de la conversación del usuario y envía un mensaje con el resultado.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 403 FORBIDDEN: El usuario no tiene permisos para realizar la acción. Se limpia la sesion y se muestra el menú de autenticación.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     * 404 NOT FOUND: No se encuentra el juego para eliminar. Se informa al usuario y se vuelve al menú de admin.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param juegoId El identificador del juego a eliminar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleEliminarJuego(Long chatId, String juegoId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.eliminarJuego(token, juegoId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
            enviarMensaje(chatId, "Juego eliminado exitosamente! Que desea hacer?", TecladoMenuPrincipalAdmin.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.FORBIDDEN) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la creación de un nuevo administrador en el sistema.
     * <p>
     * Esta función intenta registrar un nuevo administrador en el backend utilizando el nombre de usuario y
     * la contraseña proporcionada. Si el registro es exitoso, se guarda el token de autenticación y se actualiza
     * el estado de la conversación del usuario, enviando un mensaje de éxito.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 403 FORBIDDEN: El usuario no tiene permisos para realizar la acción. Se limpiar la sesion y se muestra el menú de autenticación.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     * 409 CONFLICT: El nombre de usuario ya está en uso. Se informa al usuario y se vuelve al menú de admin.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param password La contraseña del nuevo administrador.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "No tiene permisos para realizar esta accion", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "Ese nombre de usuario ya esta en uso. Que desea hacer?", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }

    }

    /**
     * Maneja la creación de una nueva publicación para el usuario.
     * <p>
     * Esta función intenta crear una nueva publicación en el backend utilizando la información
     * proporcionada por el usuario, como la carta, el título, la descripción, los intereses y el precio.
     * Luego, actualiza el estado de la conversación del usuario y envía un mensaje de éxito si la publicación
     * fue creada correctamente.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 404 NOT FOUND: No se encontró el juego o la carta asociada a la publicación. Se informa al usuario y se vuelve al menú principal.
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Publicacion creada exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                if(e.getMessage().contains("juego")) {
                    enviarMensaje(chatId, "No se encontro el juego. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                }
                if(e.getMessage().contains("carta")) {

                    enviarMensaje(chatId, "No se encontro la carta. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
                }
                return;
            }
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la solicitud del perfil del usuario.
     * <p>
     * Esta función obtiene la información del perfil del usuario, como su nombre y rol, desde el backend
     * utilizando el token JWT almacenado. Luego, actualiza el estado de la conversación del usuario y envía un mensaje
     * con la información del perfil y opciones de acción adicionales.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handlePerfil(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.PERFIL);
            PersonaDTO personaInfo = backEndClient.getPersona(token);
            enviarMensaje(chatId, "Bienvenido: "+ personaInfo.nombre() + ". Eres un " + personaInfo.rol() + ". Que desea hacer?", TecladoPerfil.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la solicitud de publicaciones del perfil del usuario.
     * <p>
     * Esta función recupera las publicaciones asociadas al usuario desde el backend utilizando el token JWT almacenado.
     * Si el usuario no tiene publicaciones, se muestra un mensaje informando de ello. Si el usuario tiene publicaciones,
     * se envían los detalles de cada publicación, junto con una imagen y botones para ver más detalles o volver al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                    enviarMensajeConFoto(chatId,text,publicacion.imagen(), TecladoVerPublicacion.get(publicacion.id()));
                }
                enviarMensaje(chatId, "Si desea ingresar a una de las publicaciones, toque el boton Ver Publicacion. Si no, toque el boton para volver al Menu Principal", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }


        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de una publicación en detalle.
     * <p>
     * Esta función recupera la información detallada de una publicación específica desde el backend utilizando el token JWT
     * del usuario. Luego, envía las imágenes asociadas a la publicación (si existen) y proporciona detalles como el título,
     * la carta asociada, el estado, la descripción, el publicador, la fecha de publicación, el precio y los intereses.
     * Dependiendo de si el usuario es el publicador o no, se muestra un teclado con opciones diferentes.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param idPubli El identificador de la publicación que se desea ver.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleVerPublicacion(Long chatId, String idPubli) throws TelegramApiException{
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de las ofertas recibidas por el usuario en su perfil.
     * <p>
     * Esta función recupera las ofertas que un usuario ha recibido desde el backend utilizando su token JWT.
     * Luego, si el usuario tiene ofertas, se envían los detalles de cada una, como el título de la publicación,
     * el nombre de la carta, el estado de la oferta, la fecha y el nombre del ofertante. Si no hay ofertas, se informa
     * al usuario con un mensaje adecuado.
     * <p>
     * Si hay ofertas disponibles, el bot muestra un botón para ver cada oferta individualmente.
     * En caso de que el usuario no quiera ver ninguna oferta, se muestra un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de las ofertas realizadas por el usuario en su perfil.
     * <p>
     * Esta función recupera las ofertas que un usuario ha realizado desde el backend utilizando su token JWT.
     * Luego, si el usuario tiene ofertas realizadas, se envían los detalles de cada una, como el título de la publicación,
     * el nombre de la carta, el estado de la oferta, la fecha y el nombre del ofertante. Si no hay ofertas, se informa
     * al usuario con un mensaje adecuado.
     * <p>
     * Si hay ofertas disponibles, el bot muestra un botón para ver cada oferta individualmente.
     * En caso de que el usuario no quiera ver ninguna oferta, se muestra un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de una oferta específica realizada por un usuario o recibida por él.
     * <p>
     * Esta función recupera los detalles de una oferta específica desde el backend utilizando el token JWT del usuario.
     * Se muestra información relevante sobre la oferta, como el título de la publicación, la fecha de la oferta,
     * el estado de la oferta, el valor ofrecido, las cartas ofrecidas y los nombres del ofertante y del publicador.
     * Dependiendo del estado de la oferta, se muestran diferentes botones para interactuar con la oferta o volver al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param idOferta El identificador único de la oferta que se desea visualizar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleVerOferta(Long chatId, String idOferta) throws TelegramApiException {
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
                    enviarMensajeConFoto(chatId,textoFinal,oferta.imagen(), TecladoOfertaPublicador.get(idOferta));
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la creación de una oferta por parte de un usuario.
     * <p>
     * Esta función recupera el token JWT del usuario y utiliza el backend para crear una nueva oferta basada en los datos almacenados
     * en la sesión del usuario. Si la oferta se crea exitosamente, se guarda el estado de la conversación y se notifica al usuario
     * que la oferta fue creada exitosamente.
     * <p>
     * Si ocurre un error HTTP o una excepción, se maneja de la siguiente forma:
     *
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita al usuario volver a iniciar sesión.
     *
     * Para cualquier otro error, se guarda el estado de la conversación y se informa al usuario de que hubo un error al crear la oferta.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleCrearOferta(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.crearOferta(token, sesionesServicio.obtenerUltimaOferta(chatId));
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Oferta creada exitosamente! Que desea hacer?", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "Error al crear la oferta. Que desea hacer?", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la eliminación de una publicación realizada por el usuario desde su perfil.
     * <p>
     * Esta función recupera el token JWT del usuario y utiliza el backend para borrar la publicación especificada
     * por su ID. Si la publicación se elimina exitosamente, se guarda el estado de la conversación y se notifica
     * al usuario que la publicación fue eliminada correctamente.
     * <p>
     *
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita al usuario volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param idPubli El identificador único de la publicación que se desea eliminar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleBorrarPublicacion(Long chatId, String idPubli) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            backEndClient.borrarPublicacion(token, idPubli);
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            enviarMensaje(chatId, "Publicación borrada exitosamente", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId, "No se pudo borrar la publicación", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }

    }

    /**
     * Maneja la aceptación de una oferta realizada por otro usuario.
     * <p>
     * Esta función recupera el token JWT del usuario y utiliza el backend para aceptar la oferta especificada
     * por su ID. Si la oferta es aceptada exitosamente, se guarda el estado de la conversación y se notifica
     * al usuario que la oferta fue aceptada correctamente.
     * <p>
     *
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita al usuario volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param idOferta El identificador único de la oferta que se desea aceptar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleAceptarOferta(Long chatId, String idOferta) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.aceptarOferta(token, idOferta);
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"Oferta Aceptada Exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"No se pudo aceptar la oferta.", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja el rechazo de una oferta realizada por otro usuario.
     * <p>
     * Esta función recupera el token JWT del usuario y utiliza el backend para rechazar la oferta especificada
     * por su ID. Si la oferta es rechazada exitosamente, se guarda el estado de la conversación y se notifica
     * al usuario que la oferta fue rechazada correctamente.
     * <p>
     *
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita al usuario volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param idOferta El identificador único de la oferta que se desea rechazar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleRechazarOferta(Long chatId, String idOferta) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            backEndClient.rechazarOferta(token, idOferta);
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"Oferta Rechazada Exitosamente!", TecladoMenuPrincipalUsuario.get());
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiarOferta(chatId);
            sesionesServicio.limpiarFiltros(chatId);
            sesionesServicio.limpiarPublicacion(chatId);
            sesionesServicio.limpiarListaCartas(chatId);
            sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
            enviarMensaje(chatId,"No se pudo rechazar la oferta.", TecladoMenuPrincipalUsuario.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void handlePublicaciones(Long chatId) throws TelegramApiException {
        handleVerPagina(chatId,1, new FiltrosPublicacion());
    }

    /**
     * Maneja la visualización de las publicaciones paginadas, con la posibilidad de aplicar filtros de búsqueda.
     * <p>
     * Esta función recupera las publicaciones paginadas desde el backend, utilizando el token JWT del usuario,
     * el número de página y los filtros de búsqueda especificados. Si hay publicaciones disponibles, se envían los
     * detalles de cada una, como el título de la publicación, el título de la carta y el estado de la carta, junto con
     * la imagen correspondiente. Si no hay publicaciones, se informa al usuario con un mensaje adecuado.
     * <p>
     * Además, se proporcionan botones para navegar entre las páginas de publicaciones, aplicar filtros o volver al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param pagina El número de página de las publicaciones que se desea ver.
     * @param filtros Los filtros de búsqueda que se aplican a las publicaciones.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void handleVerPagina(Long chatId, int pagina, FiltrosPublicacion filtros) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            PaginacionPublicacionDTO publicacionesPaginado = backEndClient.getPublicaciones(token,pagina, filtros);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_PUBLICACIONES);
            if(publicacionesPaginado.publicaciones().isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de los juegos disponibles con filtros aplicados para un usuario.
     * <p>
     * Esta función recupera la lista de juegos desde el backend utilizando el token JWT del usuario, y permite aplicar
     * filtros para mostrar los juegos. Si se encuentran juegos disponibles, se envían los detalles de cada uno, incluyendo
     * el ID y el nombre del juego, junto con un botón para seleccionar el juego y aplicar filtros adicionales.
     * Si no se encuentran juegos, se informa al usuario con un mensaje adecuado.
     * <p>
     * También se proporciona un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarJuegosFiltro(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_JUEGO);
            if(juegos.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                for(JuegoDTO juego : juegos){
                    String texto = i + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoFiltro.get(juego.id(), juego.nombre()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de los juegos disponibles para la creación de cartas en el sistema de administración.
     * <p>
     * Esta función recupera la lista de juegos desde el backend utilizando el token JWT del usuario, y permite que el
     * administrador seleccione un juego para la creación de cartas. Si se encuentran juegos disponibles, se envían los
     * detalles de cada uno, incluyendo el ID y el nombre del juego, junto con un botón para seleccionar el juego y
     * continuar con la creación de la carta. Si no se encuentran juegos, se informa al administrador con un mensaje adecuado.
     * <p>
     * También se proporciona un botón para regresar al menú principal del administrador.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                int i = 1;
                for(JuegoDTO juego : juegos){
                    String texto = i + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoCreacionCarta.get(juego.id(), juego.nombre()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de los juegos disponibles para su eliminación en el sistema de administración.
     * <p>
     * Esta función recupera la lista de juegos desde el backend utilizando el token JWT del usuario, y permite que el
     * administrador seleccione un juego para su eliminación. Si se encuentran juegos disponibles, se envían los
     * detalles de cada uno, incluyendo el ID y el nombre del juego, junto con un botón para seleccionar el juego y
     * proceder con la eliminación. Si no se encuentran juegos, se informa al administrador con un mensaje adecuado.
     * <p>
     * También se proporciona un botón para regresar al menú principal del administrador.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                int i = 1;
                for(JuegoDTO juego : juegos){
                    String texto = i + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoEliminacion.get(juego.id(), juego.nombre()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de los juegos disponibles para la creación de una nueva publicación en el sistema.
     * <p>
     * Esta función recupera la lista de juegos desde el backend utilizando el token JWT del usuario, y permite que el
     * usuario seleccione un juego para crear una publicación relacionada con dicho juego. Si se encuentran juegos
     * disponibles, se envían los detalles de cada uno, incluyendo el ID y el nombre del juego, junto con un botón para
     * seleccionar el juego y proceder con la creación de la publicación. Si no se encuentran juegos, se informa al
     * usuario con un mensaje adecuado.
     * <p>
     * También se proporciona un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarJuegosCreacionPublicacion(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_JUEGO_PUBLICACION);
            if(juegos.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                for(JuegoDTO juego : juegos){
                    String texto = i + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoCreacionPublicacion.get(juego.id(), juego.nombre()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra al usuario una lista de juegos disponibles para seleccionar al crear una solicitud de carta.
     * <p>
     * Si no hay juegos disponibles, limpia los datos relacionados a la sesión del usuario y lo redirige al menú principal.
     * Si hay juegos, los muestra uno por uno con sus respectivos botones para selección.
     * En caso de error de autenticación (401), limpia completamente la sesión y solicita al usuario volver a iniciar sesión.
     * Si ocurre cualquier otro error, también se reinicia la sesión y se ofrece el menú de autenticación.
     *
     * @param chatId ID del chat del usuario en Telegram.
     * @throws TelegramApiException si ocurre un error al enviar mensajes a través de la API de Telegram.
     */
    private void mostrarJuegosCreacionSolicitudCarta(Long chatId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<JuegoDTO> juegos = backEndClient.getJuegos(token);
            if(juegos.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ningun Juego", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                for(JuegoDTO juego : juegos){
                    String texto = i + ". " + juego.nombre() + ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionJuegoSolicitudCarta.get(juego.id(), juego.nombre()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Maneja la visualización de las cartas disponibles para la creación de una nueva publicación en el sistema.
     * <p>
     * Esta función recupera la lista de cartas asociadas a un juego específico desde el backend utilizando el token JWT
     * del usuario. Permite que el usuario seleccione una carta para asociarla a una publicación. Si se encuentran cartas
     * disponibles, se envían los detalles de cada una, incluyendo el ID de la carta, el nombre de la carta, y el juego
     * al que pertenece, junto con un botón para seleccionar la carta y proceder con la creación de la publicación.
     * Si no se encuentran cartas, se informa al usuario con un mensaje adecuado.
     * <p>
     * También se proporciona un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @param juegoId El ID del juego al cual pertenecen las cartas que se van a mostrar.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarCartasCreacionPublicacion(Long chatId, String juegoId) throws TelegramApiException {
        try{
            String token = sesionesServicio.obtenerToken(chatId);
            List<CartaDTO> cartas = backEndClient.getCartasByJuego(token,juegoId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_CARTA_PUBLICACION);
            if(cartas.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                for(CartaDTO carta : cartas){
                    String texto = i + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaCreacionPublicacion.get(carta.cartaId(), carta.nombreCarta()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra las cartas disponibles para aplicar un filtro de publicación en el sistema.
     * <p>
     * Esta función recupera la lista de cartas de acuerdo con los filtros establecidos para el usuario. Si el filtro
     * contiene un ID de juego, solo se recuperan las cartas asociadas a ese juego. Si no se especifica ningún juego en
     * el filtro, se recuperan todas las cartas disponibles. La lista de cartas se muestra con su nombre y el juego
     * al que pertenecen, junto con un botón para seleccionar la carta y aplicar el filtro.
     * <p>
     * Si no se encuentran cartas disponibles, se informa al usuario con un mensaje adecuado y se regresa al menú principal.
     * <p>
     * También se proporciona un botón para regresar al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                for(CartaDTO carta : cartas){
                    String texto = i + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaFiltro.get(carta.cartaId(), carta.nombreCarta()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra la lista de cartas disponibles para eliminación.
     * <p>
     * Este método obtiene todas las cartas del sistema y las muestra con sus nombres y el juego al que pertenecen.
     * El admin podrá seleccionar una carta para proceder con su eliminación. Si no se encuentran cartas disponibles,
     * se notifica al admin y se regresa al menú principal de administración.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otro tipo de excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
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
                int i = 1;
                for(CartaDTO carta : cartas){
                    String texto = i + ". " + carta.nombreCarta() + ". \n" +
                            "Juego: " + carta.nombreJuego()+ ". \n";
                    enviarMensaje(chatId, texto, TecladoSeleccionCartaEliminacion.get(carta.cartaId(), carta.nombreCarta()));
                    i++;
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));

            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra la lista de cartas disponibles para el usuario.
     * <p>
     * Este método obtiene todas las cartas disponibles y las presenta al usuario con sus identificadores, nombres y los juegos a los que pertenecen.
     * Si no se encuentran cartas, se notifica al usuario y se regresa al menú principal.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarCartas(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<CartaDTO> cartas = backEndClient.getCartas(token);;

            if(cartas.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna Carta", TecladoMenuPrincipalUsuario.get());
            }
            else{
                int i = 1;
                sesionesServicio.limpiarListaCartas(chatId);
                List<CartaNumId> cartasNum = new ArrayList<>();
                String texto = "";
                for(CartaDTO carta : cartas){
                    texto += i + ". " + carta.nombreCarta() + ". Juego: "+ carta.nombreJuego()+ ". \n";
                    CartaNumId cartaNum = new CartaNumId();
                    cartaNum.setId(carta.cartaId());
                    cartaNum.setNumAsociado(i);
                    cartasNum.add(cartaNum);
                    i++;

                }
                sesionesServicio.guardarListaCartas(chatId, cartasNum);
                enviarMensaje(chatId, texto);
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));

            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra los estados disponibles para ser utilizados como filtros por el usuario.
     * <p>
     * Este método obtiene la lista de estados desde el backend y los presenta al usuario. Si no se encuentran estados,
     * se notifica al usuario y se regresa al menú principal. El usuario puede seleccionar uno de los estados disponibles como filtro.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarEstadosFiltros(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<String> estados = backEndClient.getEstados(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_FILTRO_ESTADO);
            if(estados.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    /**
     * Muestra los estados disponibles para ser utilizados en la publicación de una carta.
     * <p>
     * Este método obtiene la lista de estados desde el backend y los presenta al usuario para que los seleccione al momento de crear una publicación.
     * Si no se encuentran estados, se notifica al usuario y se regresa al menú principal.
     * El usuario puede seleccionar uno de los estados disponibles para la publicación de la carta.
     * <p>
     * En caso de errores HTTP específicos, se manejan de la siguiente forma:
     *
     * 401 UNAUTHORIZED: La sesión ha expirado. Se limpia la sesión y se solicita volver a iniciar sesión.
     *
     * Para cualquier otra excepción, se limpia la sesión y se muestra el menú de autenticación.
     *
     * @param chatId El identificador del chat de Telegram del usuario.
     * @throws TelegramApiException Si ocurre un error al enviar un mensaje a través del bot de Telegram.
     */
    private void mostrarEstadosPublicacion(Long chatId) throws TelegramApiException {
        try {
            String token = sesionesServicio.obtenerToken(chatId);
            List<String> estados = backEndClient.getEstados(token);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.ESPERANDO_SELECCION_ESTADO_PUBLICACION);
            if(estados.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
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
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void mostrarSolictudesCartaPerfil(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUDES_CARTA_PERFIL);
            String token = sesionesServicio.obtenerToken(chatId);
            List<SolicitudCartaDTO> solicitudes = backEndClient.getSolicitudesCartaByPersona(token);
            if(solicitudes.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(SolicitudCartaDTO solicitud : solicitudes){
                    String texto = "Carta: " + solicitud.nombreCarta() + "\n" +
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                            "Estado: " + solicitud.estado() + "\n" +
                            "Fecha Solicitud: " + solicitud.fechaSolicitud() + "\n";
                    if(solicitud.fechaCierre() != null){
                        texto += " Fecha Cierre: " + solicitud.fechaCierre() + "\n";
                    }
                    enviarMensaje(chatId,texto);
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void mostrarSolictudesJuegoPerfil(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUDES_JUEGO_PERFIL);
            String token = sesionesServicio.obtenerToken(chatId);
            List<SolicitudJuegoDTO> solicitudes = backEndClient.getSolicitudesJuegoByPersona(token);
            if(solicitudes.isEmpty()){
                sesionesServicio.limpiarOferta(chatId);
                sesionesServicio.limpiarFiltros(chatId);
                sesionesServicio.limpiarPublicacion(chatId);
                sesionesServicio.limpiarListaCartas(chatId);
                sesionesServicio.limpiarIdJuegoSolicitudCarta(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalUsuario.get());
            }
            else{
                for(SolicitudJuegoDTO solicitud : solicitudes){
                    String texto =
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                            "Estado: " + solicitud.estado() + "\n" +
                            "Fecha Solicitud: " + solicitud.fechaSolicitud() + "\n";
                    if(solicitud.fechaCierre() != null){
                        texto += " Fecha Cierre: " + solicitud.fechaCierre() + "\n";
                    }
                    enviarMensaje(chatId,texto);
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.USUARIO));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void mostrarTodasSolicitudesJuego(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_TODAS_SOLICITUDES_JUEGO);
            List<SolicitudJuegoDTO> solicitudes = backEndClient.getTodasSolicitudJuego(sesionesServicio.obtenerToken(chatId));
            if(solicitudes.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(SolicitudJuegoDTO solicitud : solicitudes){
                    String texto =
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                                    "Estado: " + solicitud.estado() + "\n";
                    enviarMensaje(chatId,texto, TecladoVerSolicitudJuego.get(solicitud.idSolicitud()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void mostrarSolicitudesEnEsperaJuego(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUDES_EN_ESPERA_JUEGO);
            List<SolicitudJuegoDTO> solicitudes = backEndClient.getSolicitudesJuegoEnEspera(sesionesServicio.obtenerToken(chatId));
            if(solicitudes.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(SolicitudJuegoDTO solicitud : solicitudes){
                    String texto =
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                                    "Estado: " + solicitud.estado() + "\n";
                    enviarMensaje(chatId,texto, TecladoVerSolicitudJuego.get(solicitud.idSolicitud()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void handleVerSolicitudJuego(Long chatId, String soliId) throws TelegramApiException {
        try {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUD_JUEGO);
            SolicitudJuegoDTO solicitud = backEndClient.getSolicitudJuego(sesionesServicio.obtenerToken(chatId),soliId);
            String texto =
                    "Juego: " + solicitud.nombreJuego() + "\n" +
                            "Estado: " + solicitud.estado() + "\n" +
                            "Fecha Solicitud: " + solicitud.fechaSolicitud() + "\n";
            if(solicitud.fechaCierre() != null && !solicitud.estado().equals("EN_ESPERA")){
                texto += " Fecha Cierre: " + solicitud.fechaCierre() + "\n";
                enviarMensaje(chatId,texto, TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
            else{
                enviarMensaje(chatId,texto, TecladoSolicitudJuegoAdmin.get(soliId));
            }


        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "Solicitud no encontrada, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
    }

    private void mostrarTodasSolicitudesCarta(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_TODAS_SOLICITUDES_CARTA);
            List<SolicitudCartaDTO> solicitudes = backEndClient.getTodasSolicitudCarta(sesionesServicio.obtenerToken(chatId));
            if(solicitudes.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(SolicitudCartaDTO solicitud : solicitudes){
                    String texto = "Carta: " + solicitud.nombreCarta() + "\n" +
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                                    "Estado: " + solicitud.estado() + "\n";
                    enviarMensaje(chatId,texto, TecladoVerSolicitudCarta.get(solicitud.idSolicitud()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void mostrarSolicitudesEnEsperaCarta(Long chatId) throws TelegramApiException {
        try{
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUDES_EN_ESPERA_CARTA);
            List<SolicitudCartaDTO> solicitudes = backEndClient.getSolicitudesCartaEnEspera(sesionesServicio.obtenerToken(chatId));
            if(solicitudes.isEmpty()){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "No se encontro ninguna solicitud", TecladoMenuPrincipalAdmin.get());
            }
            else{
                for(SolicitudCartaDTO solicitud : solicitudes){
                    String texto = "Carta: " + solicitud.nombreCarta() + "\n" +
                            "Juego: " + solicitud.nombreJuego() + "\n" +
                                    "Estado: " + solicitud.estado() + "\n";
                    enviarMensaje(chatId,texto, TecladoVerSolicitudCarta.get(solicitud.idSolicitud()));
                }
                enviarMensaje(chatId,"Volver al menu", TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Se cerro la sesion. Que desea hacer?", TecladoAuth.get());
        }
    }

    private void handleVerSolicitudCarta(Long chatId, String soliId) throws TelegramApiException {
        try {
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.VIENDO_SOLICITUD_CARTA);
            SolicitudCartaDTO solicitud = backEndClient.getSolicitudCarta(sesionesServicio.obtenerToken(chatId),soliId);
            String texto = "Carta: " + solicitud.nombreCarta() + "\n" +
                    "Juego: " + solicitud.nombreJuego() + "\n" +
                            "Estado: " + solicitud.estado() + "\n" +
                            "Fecha Solicitud: " + solicitud.fechaSolicitud() + "\n";
            if(solicitud.fechaCierre() != null && !solicitud.estado().equals("EN_ESPERA")){
                texto += " Fecha Cierre: " + solicitud.fechaCierre() + "\n";
                enviarMensaje(chatId,texto, TecladoVolverAlMenu.get(RolesPersona.ADMIN));
            }
            else{
                enviarMensaje(chatId,texto, TecladoSolicitudCartaAdmin.get(soliId));
            }


        }
        catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                sesionesServicio.limpiar(chatId);
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
                enviarMensaje(chatId, "Sesion vencida, vuelva a logearse", TecladoAuth.get());
                return;
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                sesionesServicio.guardarEstadoConversacion(chatId, BotState.MENU_PRINCIPAL_ADMIN);
                enviarMensaje(chatId, "Solicitud no encontrada, vuelta al menu principal", TecladoMenuPrincipalAdmin.get());
                return;
            }
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
        catch (Exception e) {
            sesionesServicio.limpiar(chatId);
            sesionesServicio.guardarEstadoConversacion(chatId, BotState.INICIO);
            enviarMensaje(chatId, "Ocurrio un error, vuelva a logearse", TecladoAuth.get());
        }
    }




}

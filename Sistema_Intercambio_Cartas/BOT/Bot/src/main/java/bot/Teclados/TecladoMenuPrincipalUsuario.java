package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoMenuPrincipalUsuario {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton perfil = new InlineKeyboardButton("Ir a Perfil");
        perfil.setCallbackData("PERFIL");

        InlineKeyboardButton publicaciones = new InlineKeyboardButton("Ir a Publicaciones");
        publicaciones.setCallbackData("PUBLICACIONES");

        InlineKeyboardButton cerrarSesion = new InlineKeyboardButton("Cerrar Sesion");
        cerrarSesion.setCallbackData("CERRAR_SESION");

        InlineKeyboardButton crear_publi = new InlineKeyboardButton("Crear Publicacion");
        crear_publi.setCallbackData("CREAR_PUBLICACION");

        InlineKeyboardButton crear_soli_juego = new InlineKeyboardButton("Crear Solicitud de Juego");
        crear_soli_juego.setCallbackData("CREAR_SOLICITUD_JUEGO");

        InlineKeyboardButton crear_soli_carta = new InlineKeyboardButton("Crear Solicitud de Carta");
        crear_soli_carta.setCallbackData("CREAR_SOLICITUD_CARTA");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(perfil),
                List.of(publicaciones),
                List.of(crear_publi),
                List.of(crear_soli_juego),
                List.of(crear_soli_carta),
                List.of(cerrarSesion)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

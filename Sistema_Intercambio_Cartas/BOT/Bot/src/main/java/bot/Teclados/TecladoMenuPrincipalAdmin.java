package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoMenuPrincipalAdmin {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton carta = new InlineKeyboardButton("Administrar Cartas");
        carta.setCallbackData("ADMIN_CARTAS");

        InlineKeyboardButton juego = new InlineKeyboardButton("Administrar Juegos");
        juego.setCallbackData("ADMIN_JUEGOS");

        InlineKeyboardButton estadisticas = new InlineKeyboardButton("Ver Estadisticas");
        estadisticas.setCallbackData("ESTADISTICAS");

        InlineKeyboardButton nuevo_admin = new InlineKeyboardButton("Crear nuevo administrador");
        nuevo_admin.setCallbackData("CREAR_ADMIN");

        InlineKeyboardButton cerrarSesion = new InlineKeyboardButton("Cerrar Sesion");
        cerrarSesion.setCallbackData("CERRAR_SESION");

        InlineKeyboardButton ver_soli_juegos = new InlineKeyboardButton("Ver Solicitudes de Juegos");
        ver_soli_juegos.setCallbackData("VER_SOLICITUDES_JUEGOS");

        InlineKeyboardButton ver_soli_cartas = new InlineKeyboardButton("Ver Solicitudes de Cartas");
        ver_soli_cartas.setCallbackData("VER_SOLICITUDES_CARTAS");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(carta),
                List.of(juego),
                List.of(estadisticas),
                List.of(nuevo_admin),
                List.of(ver_soli_juegos),
                List.of(ver_soli_cartas),
                List.of(cerrarSesion)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

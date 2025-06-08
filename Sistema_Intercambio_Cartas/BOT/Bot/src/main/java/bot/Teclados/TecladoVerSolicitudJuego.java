package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoVerSolicitudJuego {
    public static InlineKeyboardMarkup get(String soliId) {

        InlineKeyboardButton solicitud = new InlineKeyboardButton("Ver Solicitud");
        String callback = "VER_SOLICITUD_JUEGO_UNICA_"+ soliId;
        solicitud.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(solicitud)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

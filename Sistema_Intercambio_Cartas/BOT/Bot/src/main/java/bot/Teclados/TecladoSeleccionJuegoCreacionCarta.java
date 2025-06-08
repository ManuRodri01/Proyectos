package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoSeleccionJuegoCreacionCarta {
    public static InlineKeyboardMarkup get(String juegoId, String nombre) {

        InlineKeyboardButton juego = new InlineKeyboardButton("Seleccionar " + nombre);
        String callback = "CREACION_CARTA_JUEGO_"+ juegoId;
        juego.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(juego)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

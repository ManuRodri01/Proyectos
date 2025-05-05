package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoSeleccionJuegoEliminacion {
    public static InlineKeyboardMarkup get(int juegoId, String nombre) {

        InlineKeyboardButton juego = new InlineKeyboardButton("Eliminar " + nombre);
        String callback = "ELIMINAR_JUEGO_"+ juegoId;
        juego.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(juego)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

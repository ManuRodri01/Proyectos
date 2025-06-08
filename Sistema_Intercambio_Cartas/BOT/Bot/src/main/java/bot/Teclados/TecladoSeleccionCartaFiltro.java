package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoSeleccionCartaFiltro {
    public static InlineKeyboardMarkup get(String cartaId, String nombre) {

        InlineKeyboardButton carta = new InlineKeyboardButton("Seleccionar " + nombre);
        String callback = "FILTRO_CARTA_"+ cartaId;
        carta.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(carta)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

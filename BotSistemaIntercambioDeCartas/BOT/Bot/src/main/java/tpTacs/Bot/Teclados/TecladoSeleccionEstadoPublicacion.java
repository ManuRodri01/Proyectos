package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoSeleccionEstadoPublicacion {
    public static InlineKeyboardMarkup get(String nombreEstado) {

        InlineKeyboardButton estado = new InlineKeyboardButton("Seleccionar " + nombreEstado);
        String callback = "PUBLICACION_ESTADO_"+ nombreEstado;
        estado.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(estado)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

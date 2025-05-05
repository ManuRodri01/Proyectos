package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoVerPublicacion {
    public static InlineKeyboardMarkup get(int publiId) {

        InlineKeyboardButton publicacion = new InlineKeyboardButton("Ver Publicación");
        String callback = "VER_PUBLICACION_"+ publiId;
        publicacion.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(publicacion)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

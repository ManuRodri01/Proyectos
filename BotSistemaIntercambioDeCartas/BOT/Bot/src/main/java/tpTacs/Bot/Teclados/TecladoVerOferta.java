package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoVerOferta {
    public static InlineKeyboardMarkup get(int publiId) {

        InlineKeyboardButton oferta = new InlineKeyboardButton("Ver Oferta");
        String callback = "VER_OFERTA_"+ publiId;
        oferta.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(oferta)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

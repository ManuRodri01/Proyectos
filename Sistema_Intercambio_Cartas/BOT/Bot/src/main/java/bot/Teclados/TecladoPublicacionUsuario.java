package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoPublicacionUsuario {
    public static InlineKeyboardMarkup get(String publiId) {

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        InlineKeyboardButton oferta = new InlineKeyboardButton("Hacer Oferta");
        String callback = "REALIZAR_OFERTA_" + publiId;
        oferta.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(oferta),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoCreacionOferta {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton cartas = new InlineKeyboardButton("Ofrecer solo Cartas");
        cartas.setCallbackData("OFERTA_CARTAS");

        InlineKeyboardButton dinero = new InlineKeyboardButton("Ofrecer solo Dinero");
        dinero.setCallbackData("OFERTA_DINERO");

        InlineKeyboardButton dinero_cartas = new InlineKeyboardButton("Ofrecer Dinero y Cartas");
        dinero_cartas.setCallbackData("OFERTA_DINERO_CARTAS");

        InlineKeyboardButton menu = new InlineKeyboardButton("Ir al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(cartas, dinero),
                List.of(dinero_cartas),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

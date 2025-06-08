package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoPublicacionPublicador {
    public static InlineKeyboardMarkup get(String idPublicacion) {

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        InlineKeyboardButton borrar = new InlineKeyboardButton("Borrar Publicacion");
        String callback = "BORRAR_PUBLICACION_" + idPublicacion;
        borrar.setCallbackData(callback);

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(menu),
                List.of(borrar)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

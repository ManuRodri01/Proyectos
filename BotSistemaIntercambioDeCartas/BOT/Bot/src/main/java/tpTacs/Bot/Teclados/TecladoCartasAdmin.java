package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoCartasAdmin {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton crear = new InlineKeyboardButton("Crear Carta");
        crear.setCallbackData("CREAR_CARTA");

        InlineKeyboardButton borrar = new InlineKeyboardButton("Eliminar una carta");
        borrar.setCallbackData("BORRAR_CARTA");

        InlineKeyboardButton menu = new InlineKeyboardButton("Ir al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL_ADMIN");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(crear, borrar),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

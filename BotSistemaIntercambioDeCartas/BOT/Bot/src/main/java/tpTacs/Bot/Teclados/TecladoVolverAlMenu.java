package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tpTacs.Bot.RolesPersona;

import java.util.List;

public class TecladoVolverAlMenu {
    public static InlineKeyboardMarkup get(RolesPersona rolPersona) {

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al menu principal");
         switch (rolPersona) {
             case ADMIN:
                 menu.setCallbackData("MENU_PRINCIPAL_ADMIN");
                 break;
             case USUARIO:
                 menu.setCallbackData("MENU_PRINCIPAL");
                 break;
             default:
                 menu.setCallbackData("INICIO");
                 break;
         }

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

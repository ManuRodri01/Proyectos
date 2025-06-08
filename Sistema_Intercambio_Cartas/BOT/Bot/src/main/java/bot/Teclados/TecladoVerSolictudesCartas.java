package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoVerSolictudesCartas {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton ver_todas = new InlineKeyboardButton("Ver todas las Solicitudes");
        ver_todas.setCallbackData("VER_TODAS_SOLICITUDES_CARTAS");

        InlineKeyboardButton ver_en_espera = new InlineKeyboardButton("Ver Solicitudes En Espera");
        ver_en_espera.setCallbackData("VER_SOLICITUDES_CARTAS_EN_ESPERA");

        InlineKeyboardButton menu = new InlineKeyboardButton("Ir al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL_ADMIN");


        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(ver_todas),
                List.of(ver_en_espera),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

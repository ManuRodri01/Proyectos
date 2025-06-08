package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoCreacionPublicacion {
    public static InlineKeyboardMarkup get(String opcional_publi) {
        InlineKeyboardButton si_opcional = new InlineKeyboardButton("Si");
        String siText = "SI_PUBLICACION_" + opcional_publi;
        si_opcional.setCallbackData(siText);

        InlineKeyboardButton no_opcional = new InlineKeyboardButton("No");
        String noText = "NO_PUBLICACION_" + opcional_publi;
        no_opcional.setCallbackData(noText);

        InlineKeyboardButton menu = new InlineKeyboardButton("Ir al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(si_opcional, no_opcional),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

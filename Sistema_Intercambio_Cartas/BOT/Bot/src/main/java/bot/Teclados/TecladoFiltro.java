package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoFiltro {
    public static InlineKeyboardMarkup get(String filtro) {
        InlineKeyboardButton si_filtro = new InlineKeyboardButton("Si");
        String siText = "SI_FILTRO_" + filtro;
        si_filtro.setCallbackData(siText);

        InlineKeyboardButton no_filtro = new InlineKeyboardButton("No");
        String noText = "NO_FILTRO_" + filtro;
        no_filtro.setCallbackData(noText);

        InlineKeyboardButton menu = new InlineKeyboardButton("Ir al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(si_filtro, no_filtro),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

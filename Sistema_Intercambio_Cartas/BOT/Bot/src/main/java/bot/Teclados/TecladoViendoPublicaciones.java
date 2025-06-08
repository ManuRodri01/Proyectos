package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoViendoPublicaciones {
    public static InlineKeyboardMarkup get(int pagAnterior, int pagSiguiente) {
        InlineKeyboardButton anterior = new InlineKeyboardButton("Pagina Anterior");
        String anteriorText = "VER_PAGINA_" + pagAnterior;
        anterior.setCallbackData(anteriorText);

        InlineKeyboardButton siguiente = new InlineKeyboardButton("Pagina Siguiente");
        String siguienteText = "VER_PAGINA_" + pagSiguiente;
        siguiente.setCallbackData(siguienteText);

        InlineKeyboardButton filtros = new InlineKeyboardButton("Aplicar Filtros");
        filtros.setCallbackData("APLICAR_FILTROS");

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al Menu Principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas;

        if (pagAnterior < 0 && pagSiguiente < 0) {
            filas = List.of(
                    List.of(filtros),
                    List.of(menu));
        }
        else if (pagAnterior < 0 && pagSiguiente > 0) {
            filas = List.of(
                    List.of(siguiente),
                    List.of(filtros),
                    List.of(menu)
            );
        }
        else if (pagAnterior > 0 && pagSiguiente < 0) {
            filas = List.of(
                    List.of(anterior),
                    List.of(filtros),
                    List.of(menu)
            );
        }
        else{
            filas = List.of(
                    List.of(anterior,siguiente),
                    List.of(filtros),
                    List.of(menu)
            );
        }

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

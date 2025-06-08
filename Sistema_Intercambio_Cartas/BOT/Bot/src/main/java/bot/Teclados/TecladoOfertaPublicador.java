package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoOfertaPublicador {
    public static InlineKeyboardMarkup get(String ofertaId) {
        InlineKeyboardButton aceptar = new InlineKeyboardButton("Aceptar Oferta");
        String aceptarText = "ACEPTAR_OFERTA_" + ofertaId;
        aceptar.setCallbackData(aceptarText);

        InlineKeyboardButton rechazar = new InlineKeyboardButton("Rechazar Oferta");
        String rechazarText = "RECHAZAR_OFERTA_" + ofertaId;
        rechazar.setCallbackData(rechazarText);

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al Menu Principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(aceptar, rechazar),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoSolicitudCartaAdmin {
    public static InlineKeyboardMarkup get(String soliId) {
        InlineKeyboardButton aceptar = new InlineKeyboardButton("Aceptar Solicitud");
        String aceptarText = "ACEPTAR_SOLICITUD_CARTA_" + soliId;
        aceptar.setCallbackData(aceptarText);

        InlineKeyboardButton rechazar = new InlineKeyboardButton("Rechazar Solicitud");
        String rechazarText = "RECHAZAR_SOLICITUD_CARTA_" + soliId;
        rechazar.setCallbackData(rechazarText);

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al Menu Principal");
        menu.setCallbackData("MENU_PRINCIPAL_ADMIN");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(aceptar, rechazar),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

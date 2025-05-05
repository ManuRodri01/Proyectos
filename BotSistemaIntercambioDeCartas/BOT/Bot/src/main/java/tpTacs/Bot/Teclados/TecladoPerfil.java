package tpTacs.Bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TecladoPerfil {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton publicaciones = new InlineKeyboardButton("Ver tus Publicaciones");
        publicaciones.setCallbackData("PUBLICACIONES_PERFIL");

        InlineKeyboardButton ofertas_recibidas = new InlineKeyboardButton("Ver Ofertas recibidas");
        ofertas_recibidas.setCallbackData("OFERTAS_RECIBIDAS_PERFIL");

        InlineKeyboardButton ofertas_hechas = new InlineKeyboardButton("Ver ofertas Hechas");
        ofertas_hechas.setCallbackData("OFERTAS_HECHAS_PERFIL");

        InlineKeyboardButton menu = new InlineKeyboardButton("Volver al menu principal");
        menu.setCallbackData("MENU_PRINCIPAL");

        List<List<InlineKeyboardButton>> filas = List.of(
                List.of(publicaciones),
                List.of(ofertas_recibidas),
                List.of(ofertas_hechas),
                List.of(menu)
        );

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;
    }
}

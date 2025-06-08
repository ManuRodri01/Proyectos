package bot.Teclados;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.List;

public class TecladoAuth {
    public static InlineKeyboardMarkup get() {
        InlineKeyboardButton login = new InlineKeyboardButton("Login");
        login.setCallbackData("LOGIN");

        InlineKeyboardButton register = new InlineKeyboardButton("Registrase");
        register.setCallbackData("REGISTER");

        List<InlineKeyboardButton> fila = List.of(login, register);
        List<List<InlineKeyboardButton>> filas = List.of(fila);

        InlineKeyboardMarkup teclado = new InlineKeyboardMarkup();
        teclado.setKeyboard(filas);

        return teclado;

    }
}

package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.Service.TelegramBotService;


public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;


    private final TelegramBotService botService;

    public TelegramBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botService = new TelegramBotService(this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    /**
     * Funcion principal del bot, que se ejecuta cuando el mismo recibe un mensaje.
     * En caso de que el mensaje sea uno normal (texto que el usuario le manda al bot) se ejecuta el metodo handleMensaje.
     * En caso de que el mensaje recibido sea un callback (lo que recibe el bot cuando el usuario interactua con unos de los botones del chat) se ejecuta el metodo handleCallback
     * @param update objeto Update que representa la interaccion del usuario con el bot y contiene toda la inforamcion de la misma
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage() != null && update.getMessage().hasText()) {
            try {
                botService.handleMensaje(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if(update.hasCallbackQuery()) {
            try {
                botService.handleCallBack(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

    }


}

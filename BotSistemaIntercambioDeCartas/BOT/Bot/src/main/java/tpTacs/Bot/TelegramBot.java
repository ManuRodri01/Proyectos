package tpTacs.Bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tpTacs.Bot.Service.TelegramBotService;


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

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage() != null && update.getMessage().hasText()) {
            try {
                botService.handleMessage(update);
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

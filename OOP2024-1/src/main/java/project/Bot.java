package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {

    public String getBotUsername() {
        return "CyberNews_bot";
    }

    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();
                String userMessage = inMessage.getText();

                if (userMessage.equalsIgnoreCase("/latestnews")) {
                    Api api = new Api();
                    String news = api.fetchLatestNews(); // Получаем последние новости
                    sendMessage(chatId, news);
                } else {

                    ListOfCommands commandsList = new ListOfCommands();
                    String message = commandsList.findCommand(userMessage);
                    sendMessage(chatId, message);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String messageText) throws TelegramApiException {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(chatId);
        outMessage.setText(messageText);
        execute(outMessage);
    }
}


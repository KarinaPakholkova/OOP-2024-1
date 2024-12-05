package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.commands.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Bot extends TelegramLongPollingBot {

    Map<String, Action> actions = Map.of(
            "/category", new CategoryNewsAction(),
            "/latestnews", new LatestNewsAction(),
            "/mylikednews", new LikedNewsAction(),
            "/addmailinglist", new AddMailingListAction()
    );

    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();


    public String getBotUsername() {
        return "CyberNews_bot";
    }

    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String userMessage = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (actions.containsKey(userMessage)) {
                var msg = actions.get(userMessage).handle(update);
                bindingBy.put(chatId, userMessage);
                sendMessage(msg);
                if (userMessage.equals("/latestnews") || userMessage.equals("/mylikednews")){
                    String message;
                    if (userMessage.equals("/latestnews")) {
                        message = "Если хотите сохранить новость, введите ее номер, если нет - введите /q";
                    }
                    else {
                        message = "Если хотите удалить новость, введите ее номер, если нет - введите /q";
                    }
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(message);

                    sendMessage(sendMessage);
                }
            } else if (bindingBy.containsKey(chatId)) {
                var msg = actions.get(bindingBy.get(chatId)).callback(update);
                bindingBy.remove(chatId);
                sendMessage(msg);
            } else {
                ListOfCommands commandsList = new ListOfCommands();
                String message = commandsList.findCommand(userMessage);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);

                sendMessage(sendMessage);
            }
        }
    }

    private void sendMessage(BotApiMethod msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

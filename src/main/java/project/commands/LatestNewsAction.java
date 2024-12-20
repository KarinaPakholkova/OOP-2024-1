package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.API.Api;
import project.database.DatabaseManager;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatestNewsAction implements Action {

    Api api = new Api();
    String[] headlines = new String[5];
    List<AbstractMap.SimpleEntry<String, String>> newsList = api.fetchLatestNews();
    static final Map<String, String> buttonTextToUrlMap = new HashMap<>();

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();

        StringBuilder newsText = new StringBuilder("Вот последние новости:\n");
        for (int i = 0; i < newsList.size(); i++) {
            AbstractMap.SimpleEntry<String, String> news = newsList.get(i);
            newsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
            buttonTextToUrlMap.put(String.valueOf(i + 1), news.getValue()); // Сохраняем соответствие
            headlines[i] = news.getKey();

        }
        return new SendMessage(chatId, newsText.toString());
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String userMessage = msg.getText();
        if (userMessage.equals("/q")){
            return null;
        } else if (userMessage.equals("/rate"))
        {
            RateAction rateAction = new RateAction();
            return rateAction.callback(update);
        } else {
            Long userId = update.getMessage().getFrom().getId();
            // Обработка нажатия на кнопку с новостью
            DatabaseManager insert = new DatabaseManager();
            String likedNewsUrl = buttonTextToUrlMap.get(userMessage);
            insert.insertLikedNew(userId, headlines[Integer.parseInt(String.valueOf(userMessage)) - 1], likedNewsUrl);
            String text = "Вы выбрали новость " + userMessage + "\nСсылка: " + likedNewsUrl;
            return new SendMessage(chatId, text);
        }
    }
}

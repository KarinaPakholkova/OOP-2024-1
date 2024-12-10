package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.DatabaseManager;

import java.util.AbstractMap;
import java.util.List;

public class LikedNewsAction implements Action {

    DatabaseManager dbManager = new DatabaseManager();
    StringBuilder likedNewsText = new StringBuilder();

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        Long userId = update.getMessage().getFrom().getId();
        List<AbstractMap.SimpleEntry<String, String>> likedNewsList = dbManager.selectNews(userId);

        likedNewsText.setLength(0);

        if (likedNewsList.isEmpty()) {
            likedNewsText.append("У вас нет сохраненных новостей.");
        } else {
            likedNewsText.append("Вот ваши сохраненные новости:\n");
            for (int i = 0; i < likedNewsList.size(); i++) {
                AbstractMap.SimpleEntry<String, String> news = likedNewsList.get(i);
                likedNewsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
            }
        }
        return new SendMessage(chatId, likedNewsText.toString());
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String userMessage = msg.getText();
        Long userId = update.getMessage().getFrom().getId();
        String text;

        List<AbstractMap.SimpleEntry<String, String>> likedNewsList = dbManager.selectNews(userId);

        if (likedNewsList.isEmpty()) {
            text = "У вас нет сохраненных новостей.";
            return new SendMessage(chatId, text);
        }

        int newsIndex = Integer.parseInt(userMessage) - 1;

        if (newsIndex >= 0 && newsIndex < likedNewsList.size()) {
            String newsTitle = likedNewsList.get(newsIndex).getKey();
            String newsUrl = likedNewsList.get(newsIndex).getValue();

            dbManager.deleteLikedNew(userId, newsUrl);

            text = "Новость \"" + newsTitle + "\" была удалена.";
        } else {
            text = "Неверный номер. Пожалуйста, введите номер от 1 до " + likedNewsList.size() + ".";
        }
        return new SendMessage(chatId, text);
    }
}


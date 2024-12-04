package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import project.API.Api;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryNewsAction implements Action{

    Api apiCategories = new Api();

    List<String> listOfCategory = new ArrayList<>(Arrays.asList(
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"));
    StringBuilder categoryNewsText = new StringBuilder();

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String text = "Выберите категорию: business, entertainment, general, health, science, sports, technology";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String category = msg.getText();

        if (listOfCategory.contains(category)) {
            List<AbstractMap.SimpleEntry<String, String>> categoryNewsList = apiCategories.fetchNewsCategory(category);
            categoryNewsText.append("Вот новости для категории '" + category + "':\n");
            for (int i = 0; i < categoryNewsList.size(); i++) {
                AbstractMap.SimpleEntry<String, String> news = categoryNewsList.get(i);
                categoryNewsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
            }
        } else {
            categoryNewsText.append("Нет новостей для этой категории.\n" +
                    "Существующие категории: business, entertainment, general, health, science, sports, technology");
        }


        return new SendMessage(chatId, categoryNewsText.toString());
    }
}

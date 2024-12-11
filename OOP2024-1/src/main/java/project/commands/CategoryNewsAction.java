package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import project.API.Api;
import project.auxiliaryFunctions.CreateString;

public class CategoryNewsAction implements Action{

    Api apiCategories = new Api();
    CreateString categoryObj = new CreateString();

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

        categoryNewsText.setLength(0);

        if (listOfCategory.contains(category)) {
            categoryNewsText = categoryObj.printCategoryNews(category);
        } else {
            categoryNewsText.append("Нет новостей для этой категории.\n" +
                    "Существующие категории: business, entertainment, general, health, science, sports, technology");
        }
        return new SendMessage(chatId, categoryNewsText.toString());
    }
}

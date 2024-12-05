package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.BDForMailingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMailingListAction implements Action {
    BDForMailingList dbManager = new BDForMailingList();

    List<String> listOfCategory = new ArrayList<>(Arrays.asList(
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"));

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String text = "Выберите категорию для рассылки: business, entertainment, general, health, science, sports, technology";

        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String category = msg.getText();
        Long userId = update.getMessage().getFrom().getId();
        String text;

        if (listOfCategory.contains(category)) {
            dbManager.insertUser(userId, category);
            text = "Рассылка успешно подключена";
        }
        else {
            text = "Такой категории не существует.\n" +
                    "Существующие категории: business, entertainment, general, health, science, sports, technology";
        }

        return new SendMessage(chatId, text);
    }
}

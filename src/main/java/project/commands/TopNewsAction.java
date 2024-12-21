package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.DatabaseManager;
import project.database.RatingDataBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopNewsAction implements Action {
    RatingDataBase DBManagerRating = new RatingDataBase();
    static final Map<String, String> topNewsMap = new HashMap<>(); // Map to store top news with their indices
    DatabaseManager DBManagerLike = new DatabaseManager();

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        List<String> topRatedUrls = DBManagerRating.getHighestRatedUrls();
        StringBuilder listOfTopNews = new StringBuilder();

        if (topRatedUrls.get(0).equals("Пока что никто ничего не оценил")) {
            listOfTopNews.append("Пока что никто ничего не оценил");
        } else if (topRatedUrls.get(0).equals("Произошла ошибка. Повторите попытку позже")) {
            listOfTopNews.append("Произошла ошибка. Повторите попытку позже");
        } else {
            listOfTopNews.append("Топ новостей:\n");
            for (int i = 0; i < topRatedUrls.size(); i++) {
                listOfTopNews.append(i + 1).append(". ").append(topRatedUrls.get(i)).append("\n");
                topNewsMap.put(String.valueOf(i + 1), topRatedUrls.get(i)); // Save the mapping of index to URL
            }
            listOfTopNews.append("\nЕсли хотите сохранить новость, введите ее номер. Если нет, введите /q");
        }
        return new SendMessage(chatId, listOfTopNews.toString());
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String userMessage = msg.getText();

        if (userMessage.equals("/q")) {
            return new SendMessage(chatId, "Вы вышли из режима сохранения новостей.");
        } else if (topNewsMap.containsKey(userMessage)) {
            Long userId = update.getMessage().getFrom().getId();
            String selectedNewsUrl = topNewsMap.get(userMessage);

            DBManagerLike.insertLikedNew(userId, " ", selectedNewsUrl.substring(0, selectedNewsUrl.length() - 12));

            String responseText = "Вы выбрали топ новость\n" + userMessage + selectedNewsUrl;
            return new SendMessage(chatId, responseText);
        } else {
            return new SendMessage(chatId, "Неверный ввод. Пожалуйста, введите номер новости или /q для выхода.");
        }
    }
}

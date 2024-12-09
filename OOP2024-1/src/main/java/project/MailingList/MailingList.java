package project.MailingList;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.API.Api;
import project.Bot;
import project.database.BDForMailingList;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MailingList implements Runnable {
    BDForMailingList dbManager = new BDForMailingList();
    Api apiCategories = new Api();
    private final Bot bot;

    List<String> listOfErrors = new ArrayList<>(Arrays.asList(
            "Не удалось получить новости. Статус: 400",
            "Не удалось получить новости. Статус: 401",
            "Не удалось получить новости. Статус: 429",
            "Не удалось получить новости. Статус: 500",
            "Ошибка при получении новостей."
    ));

    public MailingList(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            sendHourlyMessage();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void startHourlyMessageTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
    }

    void sendHourlyMessage() throws TelegramApiException {
        List<AbstractMap.SimpleEntry<String, String>> chatIds = dbManager.selectMailingList();
        for (AbstractMap.SimpleEntry<String, String> entry : chatIds) {
            String chatId = entry.getKey();
            String category = entry.getValue();
            StringBuilder messageText = new StringBuilder();
            List<AbstractMap.SimpleEntry<String, String>> categoryNewsList = apiCategories.fetchNewsCategory(category);

            messageText.append("Рассылка новостей по категории '").append(category).append("':\n");

            for (int i = 0; i < categoryNewsList.size(); i++) {
                AbstractMap.SimpleEntry<String, String> news = categoryNewsList.get(i);
                messageText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
            }

            if (categoryNewsList.isEmpty() || containsErrorMessage(categoryNewsList)) {
                sendMessage(chatId, "К сожалению, не удалось получить новости для категории: " + category);
            } else {
                sendMessage(chatId, messageText.toString());
            }
        }
    }

    private boolean containsErrorMessage(List<AbstractMap.SimpleEntry<String, String>> messageText) {
        for (AbstractMap.SimpleEntry<String, String> entry : messageText) {
            if (listOfErrors.contains(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    private void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.execute(message);
    }
}
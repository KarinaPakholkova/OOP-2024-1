package project.MailingList;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.API.Api;
import project.Bot;
import project.auxiliaryFunctions.CreateString;
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
    CreateString categoryObj = new CreateString();
    private final Bot bot;
    StringBuilder messageText = new StringBuilder();

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
            messageText.setLength(0);
            messageText = categoryObj.printCategoryNews(category);

            boolean hasError = listOfErrors.stream().anyMatch(messageText.toString()::contains);

            if (messageText.isEmpty() || hasError) {
                sendMessage(chatId, "К сожалению, не удалось получить новости для категории: " + category);
            } else {
                sendMessage(chatId, messageText.toString());
            }
        }
    }

    private void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.execute(message);
    }
}

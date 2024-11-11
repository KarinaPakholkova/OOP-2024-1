package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class Bot extends TelegramLongPollingBot {
    private static final Map<String, String> buttonTextToUrlMap = new HashMap<>();


    public String getBotUsername() {
        return "CyberNews_bot";
    }

    public String getBotToken() {
        return System.getenv("TOKEN");
    }
    private void sendMessage(SendMessage message) throws TelegramApiException {
        execute(message);
    }
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();
                String userMessage = inMessage.getText();

                if (userMessage.equalsIgnoreCase("/latestnews")) {
                    Api api = new Api();
                    List<SimpleEntry<String, String>> newsList = api.fetchLatestNews(); // Получаем последние новости


                    // Отправляем текст новостей
                    StringBuilder newsText = new StringBuilder("Вот последние новости:\n");
                    for (int i = 0; i < newsList.size(); i++) {
                        SimpleEntry<String, String> news = newsList.get(i);
                        newsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
                        buttonTextToUrlMap.put("like №" + (i + 1), news.getValue()); // Сохраняем соответствие
                    }
                    // Создаем и отправляем сообщение с новостями
                    SendMessage newsMessage = new SendMessage();
                    newsMessage.setChatId(chatId);
                    newsMessage.setText(newsText.toString());
                    sendMessage(newsMessage);

                    // Теперь отправляем клавиатуру с кнопками
                    ReplyKeyboardMarkup keyboard = Keyboard.getNewsKeyboard(newsList);
                    SendMessage keyboardMessage = new SendMessage();
                    keyboardMessage.setChatId(chatId);
                    keyboardMessage.setText("Выберите понравившуюся новость:");
                    keyboardMessage.setReplyMarkup(keyboard);
                    sendMessage(keyboardMessage);

                } else if (buttonTextToUrlMap.containsKey(userMessage)) {
                    // Обработка нажатия на кнопку с новостью
                    String likedNewsUrl = buttonTextToUrlMap.get(userMessage);
                    sendMessage(chatId, "Вы выбрали: " + userMessage + "\nСсылка: " + likedNewsUrl);
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
    
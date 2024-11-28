package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.database.DatabaseManager;

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
    String[] headlines = new String[5];
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
                        buttonTextToUrlMap.put(String.valueOf(i + 1), news.getValue()); // Сохраняем соответствие
                        headlines[i] = news.getKey();
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
                    DatabaseManager insert = new DatabaseManager();
                    Long userId = update.getMessage().getFrom().getId();
                    String likedNewsUrl = buttonTextToUrlMap.get(userMessage);
                    insert.insertLikedNew(userId, headlines[Integer.parseInt(String.valueOf(userMessage)) - 1], likedNewsUrl);
                    sendMessage(chatId, "Вы выбрали новость " + userMessage + "\nСсылка: " + likedNewsUrl);

                } else if (userMessage.equalsIgnoreCase("/mylikednews")) {
                    DatabaseManager dbManager = new DatabaseManager();
                    Long userId = update.getMessage().getFrom().getId();
                    List<SimpleEntry<String, String>> likedNewsList = dbManager.selectNews(userId);
                    // Отправляем текст сохраненных новостей
                    StringBuilder likedNewsText = new StringBuilder("Вот ваши сохраненные новости:\n");
                    if (likedNewsList.isEmpty()) {
                        likedNewsText.append("У вас нет сохраненных новостей.");
                    } else {
                        for (int i = 0; i < likedNewsList.size(); i++) {
                            SimpleEntry<String, String> news = likedNewsList.get(i);
                            likedNewsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
                        }
                    }
                    // Создаем и отправляем сообщение с сохраненными новостями
                    SendMessage likedNewsMessage = new SendMessage();
                    likedNewsMessage.setChatId(chatId);
                    likedNewsMessage.setText(likedNewsText.toString());
                    sendMessage(likedNewsMessage);
                }

                else {
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

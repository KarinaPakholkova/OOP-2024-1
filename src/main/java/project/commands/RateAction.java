package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.RatingDataBase;

import java.util.List;

public class RateAction implements Action {

    RatingDataBase ratingDB = new RatingDataBase();

    @Override
    public BotApiMethod handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String text = "Выберите новость, которую хотите оценить, и укажите через пробел оценку от 1 до 5.";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String userMessage = msg.getText();

        try {
            String[] parts = userMessage.split(" ");
            if (parts.length != 2) {
                return new SendMessage(chatId, "Неверный формат. Укажите номер статьи и оценку через пробел (например: 1 5).");
            }

            int articleNumber = Integer.parseInt(parts[0]);
            int rating = Integer.parseInt(parts[1]);

            if (rating < 1 || rating > 5) {
                return new SendMessage(chatId, "Оценка должна быть числом от 1 до 5.");
            }

            String articleUrl = LatestNewsAction.buttonTextToUrlMap.get(String.valueOf(articleNumber));
            if (articleUrl == null) {
                return new SendMessage(chatId, "Статья с указанным номером не найдена. Пожалуйста, выберите корректный номер.");
            }

            String articleHeadline = LatestNewsAction.headlines[articleNumber - 1]; // Retrieve the headline

            List<Object> ratingData = ratingDB.selectRating(articleUrl);

            if (ratingData.isEmpty() || ratingData.get(0).equals("Новость не найдена")) {
                ratingDB.insertRating(articleHeadline, articleUrl, rating);
            } else {
                int currentRating = (int) ratingData.get(2);
                int currentCount = (int) ratingData.get(3);
                int newCount = currentCount + 1;
                int newRating = (currentRating * currentCount + rating) / newCount;

                ratingDB.updateRating(articleUrl, newRating, newCount);
            }

            String responseText = String.format("Вы оценили статью #%d на %d", articleNumber, rating);
            return new SendMessage(chatId, responseText);

        } catch (NumberFormatException e) {
            return new SendMessage(chatId, "Неверный формат. Укажите номер статьи и оценку через пробел (например: 1 5).");
        } catch (Exception e) {
            return new SendMessage(chatId, "Произошла ошибка при обработке вашей оценки. Попробуйте еще раз.");
        }
    }
}

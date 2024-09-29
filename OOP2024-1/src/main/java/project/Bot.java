package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    public String getBotUsername() {
        return "CyberNews";
    }

    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    public void onUpdateReceived(Update update) {
        try{
            if(update.hasMessage() && update.getMessage().hasText())
            {

                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                String answer = parseMessage(inMess.getText());
                SendMessage outMess = new SendMessage();
                outMess.setChatId(chatId);
                outMess.setText(answer);

                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String parseMessage(String textMsg) {
        String answer;

        if (textMsg.equals("/start"))
            answer = "Приветствую, это бот агрегатор новостей. Напиши /info, чтобы получить больше информации";
        else if (textMsg.equals("/info"))
            answer = "Бот-агрегатор новостей — ваш персональный помощник, который собирает самые важные и актуальные события из разных источников." +
                    "Он фильтрует информацию по вашим интересам, предлагая свежие статьи, видео и аналитические материалы. Оставайтесь в курсе новостей легко и удобно!";
        else if (textMsg.equals("/help"))
            answer = "/start - перезапустить бота\n" +
                    "/help - вывести список всех команд\n" +
                    "/info - информация о боте\n" +
                    "/authors - авторы бота";
        else if (textMsg.equals("/authors"))
            answer = "Авторами проекта являются студенты 2-го курса специалитета \"Компьютерная безопасность\" Пахолкова Карина и Марченко Артем";
        else
            answer = "Команда не распознана";

        return answer;
    }
}


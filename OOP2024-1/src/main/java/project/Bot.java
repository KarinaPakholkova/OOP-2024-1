package project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {

    public String getBotUsername() {
        return "CyberNews_bot";
    }

    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    public void onUpdateReceived(Update update) {
        try{
            if(update.hasMessage() && update.getMessage().hasText())
            {
                ListOfCommands commandsList = new ListOfCommands();
                Message inMessage = update.getMessage();
                String chatId = inMessage.getChatId().toString();
                String message = commandsList.findCommand(inMessage.getText());
                SendMessage outMessage = new SendMessage();
                outMessage.setChatId(chatId);
                outMessage.setText(message);

                execute(outMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }









/*    public String parseMessage(String textMsg) {
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
    }*/
}


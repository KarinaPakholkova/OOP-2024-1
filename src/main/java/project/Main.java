package project;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import project.MailingList.MailingList;

public class Main {

    public static void main(String... args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Bot bot = new Bot();

        MailingList mailingList = new MailingList(bot);
        mailingList.startHourlyMessageTask();
    }
}

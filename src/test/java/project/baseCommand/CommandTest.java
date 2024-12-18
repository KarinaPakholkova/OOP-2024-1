package project.baseCommand;

import org.junit.jupiter.api.Test;
import project.ListOfCommands;
import static org.junit.jupiter.api.Assertions.*;

class CommandTest {
    @Test
    public void testStartCommand() {
        ListOfCommands commandsList = new ListOfCommands();
        String message = commandsList.findCommand("/start");
        assertEquals(message, "Приветствую, это бот агрегатор новостей. Напиши /info, чтобы получить больше информации");
    }

    @Test
    public void testUnknownCommand() {
        ListOfCommands commandsList = new ListOfCommands();
        String message = commandsList.findCommand("/command");
        assertEquals(message, "Неверная команда");
    }

    @Test
    public void testHelpCommand(){
        ListOfCommands commandsList = new ListOfCommands();
        String message = commandsList.findCommand("/help");
        assertTrue(message.contains("/start"));
        assertTrue(message.contains("/info"));
        assertTrue(message.contains("/help"));
        assertTrue(message.contains("/authors"));

    }

}
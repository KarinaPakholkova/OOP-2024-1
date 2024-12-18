package project.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Action {

    List<String> listOfCategory = new ArrayList<>(Arrays.asList(
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"));

    BotApiMethod handle(Update update);

    BotApiMethod callback(Update update);
}

package project;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class Keyboard {
    private static final Map<String, String> buttonTextToCommandMap = new HashMap<>();
    // поменял пока что на просто цифры ибо немного поломал все
    static {
        buttonTextToCommandMap.put("1", "1");
        buttonTextToCommandMap.put("2", "2");
        buttonTextToCommandMap.put("3", "3");
        buttonTextToCommandMap.put("4", "4");
        buttonTextToCommandMap.put("5", "5");
    }

    public static ReplyKeyboardMarkup getNewsKeyboard(List<AbstractMap.SimpleEntry<String, String>> newsList) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (int i = 0; i < newsList.size(); i++) {
            KeyboardRow row = new KeyboardRow();
            String buttonText = String.valueOf((i + 1));
            row.add(new KeyboardButton(buttonText));
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
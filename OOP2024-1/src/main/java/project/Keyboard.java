package project;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class Keyboard {
    private static final Map<String, String> buttonTextToCommandMap = new HashMap<>();

    static {
        buttonTextToCommandMap.put("like №1", "like №1");
        buttonTextToCommandMap.put("like №2", "like №2");
        buttonTextToCommandMap.put("like №3", "like №3");
        buttonTextToCommandMap.put("like №4", "like №4");
        buttonTextToCommandMap.put("like №5", "like №5");
    }

    public static ReplyKeyboardMarkup getNewsKeyboard(List<AbstractMap.SimpleEntry<String, String>> newsList) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (int i = 0; i < newsList.size(); i++) {
            KeyboardRow row = new KeyboardRow();
            String buttonText = "like №" + (i + 1);
            row.add(new KeyboardButton(buttonText));
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
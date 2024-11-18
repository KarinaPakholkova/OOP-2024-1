package project;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardTest {

    @Test
    void testGetNewsKeyboard_EmptyList() {
        // Arrange
        List<AbstractMap.SimpleEntry<String, String>> newsList = new ArrayList<>();

        // Act
        ReplyKeyboardMarkup keyboard = Keyboard.getNewsKeyboard(newsList);

        // Assert
        assertNotNull(keyboard);
        assertTrue(keyboard.getKeyboard().isEmpty(), "Keyboard should be empty for an empty news list");
    }

    @Test
    void testGetNewsKeyboard_SingleEntry() {
        // Arrange
        List<AbstractMap.SimpleEntry<String, String>> newsList = new ArrayList<>();
        newsList.add(new AbstractMap.SimpleEntry<>("Title 1", "Content 1"));

        // Act
        ReplyKeyboardMarkup keyboard = Keyboard.getNewsKeyboard(newsList);

        // Assert
        assertNotNull(keyboard);
        assertEquals(1, keyboard.getKeyboard().size(), "Keyboard should have one row for a single news entry");
        KeyboardRow row = keyboard.getKeyboard().get(0);
        assertEquals(1, row.size(), "Row should have one button");
        assertEquals("1", row.get(0).getText(), "Button text should be '1'");
    }

    @Test
    void testGetNewsKeyboard_MultipleEntries() {
        // Arrange
        List<AbstractMap.SimpleEntry<String, String>> newsList = new ArrayList<>();
        newsList.add(new AbstractMap.SimpleEntry<>("Title 1", "Content 1"));
        newsList.add(new AbstractMap.SimpleEntry<>("Title 2", "Content 2"));
        newsList.add(new AbstractMap.SimpleEntry<>("Title 3", "Content 3"));

        // Act
        ReplyKeyboardMarkup keyboard = Keyboard.getNewsKeyboard(newsList);

        // Assert
        assertNotNull(keyboard);
        assertEquals(3, keyboard.getKeyboard().size(), "Keyboard should have three rows for three news entries");

        for (int i = 0; i < newsList.size(); i++) {
            KeyboardRow row = keyboard.getKeyboard().get(i);
            assertEquals(1, row.size(), "Row should have one button");
            assertEquals(String.valueOf(i + 1), row.get(0).getText(), "Button text should be '" + (i + 1) + "'");
        }
    }

}
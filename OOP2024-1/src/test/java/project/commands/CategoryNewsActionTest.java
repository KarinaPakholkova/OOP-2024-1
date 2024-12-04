package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.API.Api;

import java.util.AbstractMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryNewsActionTest {

    private CategoryNewsAction categoryNewsAction;
    private Api mockApi;

    @BeforeEach
    void setUp() {
        mockApi = mock(Api.class); // Мокаем api
        categoryNewsAction = new CategoryNewsAction();
        categoryNewsAction.apiCategories = mockApi; // Инжектим мок
    }

    @Test
    void testBotMessageWithCategories() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        SendMessage response = (SendMessage) categoryNewsAction.handle(update);

        assertEquals("12345", response.getChatId());
        assertEquals("Выберите категорию: business, entertainment, general, health, science, sports, technology", response.getText());
    }

    @Test
    void testValidCategory() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("business");

        // Мокаем ответ API
        List<AbstractMap.SimpleEntry<String, String>> mockNews = List.of(
                new AbstractMap.SimpleEntry<>("News Title 1", "News Link 1"),
                new AbstractMap.SimpleEntry<>("News Title 2", "News Link 2")
        );
        when(mockApi.fetchNewsCategory("business")).thenReturn(mockNews);

        SendMessage response = (SendMessage) categoryNewsAction.callback(update);

        assertEquals("12345", response.getChatId());
        String expectedText = """
                Вот новости для категории 'business':
                1. News Title 1
                News Link 1
                2. News Title 2
                News Link 2
                """;
        assertEquals(expectedText, response.getText());
    }

    @Test
    void testInvalidCategory() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("invalidCategory");

        SendMessage response = (SendMessage) categoryNewsAction.callback(update);

        assertEquals("12345", response.getChatId());
        String expectedText = "Нет новостей для этой категории.\n" +
                "Существующие категории: business, entertainment, general, health, science, sports, technology";
        assertEquals(expectedText, response.getText());
    }
}
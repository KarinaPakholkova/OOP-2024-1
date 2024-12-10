package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.API.Api;
import project.auxiliaryFunctions.CreateString;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryNewsActionTest {

    private CategoryNewsAction categoryNewsAction;
    private Api mockApi;
    private CreateString mockCreateString;

    @BeforeEach
    void setUp() {
        mockApi = mock(Api.class); // Мокаем апи
        mockCreateString = mock(CreateString.class);
        categoryNewsAction = new CategoryNewsAction();
        categoryNewsAction.apiCategories = mockApi; // инжектим апи
        categoryNewsAction.categoryObj = mockCreateString;
    }

    @Test
    void testBotMessageWithCategories() {
        // мокаем сообщение
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

        StringBuilder mockCategoryNews = new StringBuilder("""
                Вот новости для категории 'business':
                1. News Title 1
                News Link 1
                2. News Title 2
                News Link 2
                """);
        when(mockCreateString.printCategoryNews("business")).thenReturn(mockCategoryNews);

        SendMessage response = (SendMessage) categoryNewsAction.callback(update);

        assertEquals("12345", response.getChatId());
        assertEquals(mockCategoryNews.toString(), response.getText());
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
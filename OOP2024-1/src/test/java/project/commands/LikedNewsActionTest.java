package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import project.database.DatabaseManager;

import java.util.AbstractMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LikedNewsActionTest {

    private LikedNewsAction likedNewsAction;
    private DatabaseManager mockDbManager;

    @BeforeEach
    void setUp() {
        mockDbManager = mock(DatabaseManager.class);
        likedNewsAction = new LikedNewsAction();
        likedNewsAction.dbManager = mockDbManager;
    }

    @Test
    void testNoLikedNews() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        // Мокаем ответ бд
        when(mockDbManager.selectNews(1L)).thenReturn(List.of());

        // Вызываем метод
        SendMessage response = (SendMessage) likedNewsAction.handle(update);

        assertEquals("12345", response.getChatId());
        assertEquals("У вас нет сохраненных новостей.", response.getText());
    }

    @Test
    void testLikedNews() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        List<AbstractMap.SimpleEntry<String, String>> mockNews = List.of(
                new AbstractMap.SimpleEntry<>("News Title 1", "News Link 1"),
                new AbstractMap.SimpleEntry<>("News Title 2", "News Link 2")
        );
        when(mockDbManager.selectNews(1L)).thenReturn(mockNews);

        SendMessage response = (SendMessage) likedNewsAction.handle(update);

        assertEquals("12345", response.getChatId());
        String expectedText = """
                Вот ваши сохраненные новости:
                1. News Title 1
                News Link 1
                2. News Title 2
                News Link 2
                """;
        assertEquals(expectedText, response.getText());
    }

    @Test
    void testDeleteWithValidIndex() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(message.getText()).thenReturn("1");

        List<AbstractMap.SimpleEntry<String, String>> mockNews = List.of(
                new AbstractMap.SimpleEntry<>("News Title 1", "News Link 1"),
                new AbstractMap.SimpleEntry<>("News Title 2", "News Link 2")
        );
        when(mockDbManager.selectNews(1L)).thenReturn(mockNews);

        SendMessage response = (SendMessage) likedNewsAction.callback(update);

        assertEquals("12345", response.getChatId());
        assertEquals("Новость \"News Title 1\" была удалена.", response.getText());

        verify(mockDbManager, times(1)).deleteLikedNew(1L, "News Link 1");
    }

    @Test
    void testDeleteWithInvalidIndex() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(message.getText()).thenReturn("5");

        List<AbstractMap.SimpleEntry<String, String>> mockNews = List.of(
                new AbstractMap.SimpleEntry<>("News Title 1", "News Link 1"),
                new AbstractMap.SimpleEntry<>("News Title 2", "News Link 2")
        );
        when(mockDbManager.selectNews(1L)).thenReturn(mockNews);

        SendMessage response = (SendMessage) likedNewsAction.callback(update);

        assertEquals("12345", response.getChatId());
        assertEquals("Неверный номер. Пожалуйста, введите номер от 1 до 2.", response.getText());

        verify(mockDbManager, never()).deleteLikedNew(anyLong(), anyString());
    }
}
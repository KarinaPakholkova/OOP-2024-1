package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.RatingDataBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateActionTest {

    private RateAction rateAction;
    private RatingDataBase mockDatabase;

    @BeforeEach
    void setUp() {
        rateAction = new RateAction();
        mockDatabase = mock(RatingDataBase.class);
        rateAction.ratingDB = mockDatabase;
    }

    @Test
    void testHandle() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        SendMessage response = (SendMessage) rateAction.handle(update);

        assertEquals("Выберите новость, которую хотите оценить, и укажите через пробел оценку от 1 до 5.", response.getText());
        assertEquals("12345", response.getChatId());
    }

    @Test
    void testCallback_ValidInput_NewRating() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("1 5");

        when(mockDatabase.selectRating("http://example.com/news1")).thenReturn(List.of("Новость не найдена"));

        LatestNewsAction.buttonTextToUrlMap.put("1", "http://example.com/news1");
        LatestNewsAction.headlines[0] = "Example News Title"; // Set a headline for the test

        SendMessage response = (SendMessage) rateAction.callback(update);

        verify(mockDatabase, times(1)).insertRating("Example News Title", "http://example.com/news1", 5);

        assertEquals("Вы оценили статью #1 на 5", response.getText());
        assertEquals("12345", response.getChatId());
    }

    @Test
    void testCallback_ValidInput_UpdateRating() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("1 4");

        when(mockDatabase.selectRating("http://example.com/news1")).thenReturn(List.of("Some Title", "http://example.com/news1", 3, 2));

        LatestNewsAction.buttonTextToUrlMap.put("1", "http://example.com/news1");

        SendMessage response = (SendMessage) rateAction.callback(update);

        verify(mockDatabase, times(1)).updateRating("http://example.com/news1", 3, 3);

        assertEquals("Вы оценили статью #1 на 4", response.getText());
        assertEquals("12345", response.getChatId());
    }

    @Test
    void testCallback_InvalidInput_FormatError() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("invalid input");

        SendMessage response = (SendMessage) rateAction.callback(update);

        assertEquals("Неверный формат. Укажите номер статьи и оценку через пробел (например: 1 5).", response.getText());
        assertEquals("12345", response.getChatId());
    }

    @Test
    void testCallback_InvalidRating_OutOfRange() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("1 10");

        SendMessage response = (SendMessage) rateAction.callback(update);

        assertEquals("Оценка должна быть числом от 1 до 5.", response.getText());
        assertEquals("12345", response.getChatId());
    }


    @Test
    void testCallback_InvalidArticleNumber() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        when(message.getText()).thenReturn("99 5");

        LatestNewsAction.buttonTextToUrlMap.clear();

        SendMessage response = (SendMessage) rateAction.callback(update);

        assertEquals("Статья с указанным номером не найдена. Пожалуйста, выберите корректный номер.", response.getText());
        assertEquals("12345", response.getChatId());
    }
}

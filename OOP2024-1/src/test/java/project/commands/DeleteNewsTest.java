package project.commands;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import project.API.Api;
import project.Bot;
import project.database.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class DeleteNewsTest {
    @InjectMocks
    private Bot bot;

    @Mock
    private DatabaseManager databaseManager;

    @Mock
    private Api api;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @BeforeAll
    public static void setUpBeforeClass() {
        mockStatic(DriverManager.class);
    }

    @AfterAll
    public static void tearDownAfterClass() {
        Mockito.clearAllCaches();
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);
    }

    @Test
    void testDeleteNewsCommand_InvalidNewsIndex() {
        // Мокаем сообщение пользователя с командой
        Update update1 = mock(Update.class);
        Message message1 = mock(Message.class);
        User user = mock(User.class);

        when(update1.hasMessage()).thenReturn(true);
        when(update1.getMessage()).thenReturn(message1);
        when(message1.getText()).thenReturn("/deletenews");
        when(message1.getChatId()).thenReturn(12345L);
        when(message1.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(12345L);

        bot.onUpdateReceived(update1);

        // Мокаем неверный индекс
        Update update2 = mock(Update.class);
        Message message2 = mock(Message.class);

        when(update2.hasMessage()).thenReturn(true);
        when(update2.getMessage()).thenReturn(message2);
        when(message2.getText()).thenReturn("5");
        when(message2.getChatId()).thenReturn(12345L);
        when(message2.getFrom()).thenReturn(user);

        // Мокаем ответ бд
        List<SimpleEntry<String, String>> likedNews = new ArrayList<>();
        likedNews.add(new SimpleEntry<>("News 1", "http://news1.com"));
        when(databaseManager.selectNews(12345L)).thenReturn(likedNews);

        bot.onUpdateReceived(update2);

        // проверяем, что не смогли вызвать метод
        verify(databaseManager, never()).deleteLikedNew(anyLong(), anyString());
    }
}

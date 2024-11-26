package project.database;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManagerTest {
    @Mock
    private Connection connection;  // Мокаем соединение с базой данных

    @Mock
    private PreparedStatement preparedStatement;  // Мокаем PreparedStatement

    @InjectMocks
    private DatabaseManager databaseManager;  // Подключаем моки к DatabaseManager

    @BeforeAll
    public static void setUpBeforeClass() {
        mockStatic(DriverManager.class);
    }

    @AfterAll
    public static void tearDownAfterClass() {
        // Снимаем статический мок
        Mockito.clearAllCaches();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        // Настраиваем поведение мока соединения
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        // Настраиваем поведение мока для DriverManager
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);
    }


    @Test
    public void testInsertLikedNew() throws SQLException {
        Long userId = 123456L;
        String headlines = "Test Headline";
        String url = "http://example.com";

        databaseManager.insertLikedNew(userId, headlines, url);

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, headlines);
        verify(preparedStatement, times(1)).setString(3, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testInsertLikedNew_SQLException() throws SQLException {
        Long userId = 123456L;
        String headlines = "Test Headline";
        String url = "http://example.com";

        doThrow(new SQLException("Database error")).when(preparedStatement).executeUpdate();

        databaseManager.insertLikedNew(userId, headlines, url);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, headlines);
        verify(preparedStatement, times(1)).setString(3, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteLikedNew() throws SQLException {
        Long userId = 123456L;
        String url = "http://example.com";

        databaseManager.deleteLikedNew(userId, url);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteLikedNew_SQLException() throws SQLException {
        Long userId = 123L;
        String url = "http://example.com";

        doThrow(new SQLException("Ошибка при удалении")).when(preparedStatement).executeUpdate();

        databaseManager.deleteLikedNew(userId, url);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }
}

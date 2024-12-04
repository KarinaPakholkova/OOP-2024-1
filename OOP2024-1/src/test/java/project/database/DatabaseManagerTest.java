package project.database;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.AbstractMap;
import java.util.List;

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
        long userId = 123456L;
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
        long userId = 123456L;
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
        long userId = 123456L;
        String url = "http://example.com";

        databaseManager.deleteLikedNew(userId, url);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteLikedNew_SQLException() throws SQLException {
        long userId = 123L;
        String url = "http://example.com";

        doThrow(new SQLException("Ошибка при удалении")).when(preparedStatement).executeUpdate();

        databaseManager.deleteLikedNew(userId, url);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSelectNews_Success() throws SQLException {
        long userId = 123456L;
        String headline = "Test Headline";
        String url = "http://example.com";

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Один результат
        when(resultSet.getString("headline")).thenReturn(headline);
        when(resultSet.getString("url")).thenReturn(url);

        // Настраиваем поведение мока для PreparedStatement
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<AbstractMap.SimpleEntry<String, String>> likedNews = databaseManager.selectNews(userId);

        // Проверяем, что результат содержит ожидаемую новость
        Assertions.assertEquals(1, likedNews.size());
        Assertions.assertEquals(headline, likedNews.getFirst().getKey());
        Assertions.assertEquals(url, likedNews.getFirst().getValue());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectNews_NoResults() throws SQLException {
        long userId = 123456L;

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false); // Нет результатов

        // Настраиваем поведение мока для PreparedStatement
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<AbstractMap.SimpleEntry<String, String>> likedNews = databaseManager.selectNews(userId);

        // Проверяем, что результат пустой
        Assertions.assertTrue(likedNews.isEmpty());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectNews_SQLException() throws SQLException {
        long userId = 123456L;

        // Настраиваем поведение мока для PreparedStatement
        doThrow(new SQLException("Database error")).when(preparedStatement).executeQuery();

        List<AbstractMap.SimpleEntry<String, String>> likedNews = databaseManager.selectNews(userId);

        // Проверяем, что результат пустой при возникновении исключения
        Assertions.assertTrue(likedNews.isEmpty());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }
}

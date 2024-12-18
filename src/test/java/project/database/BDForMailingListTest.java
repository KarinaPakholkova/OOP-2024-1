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

public class BDForMailingListTest {
    @Mock
    private Connection connection;  // Мокаем соединение с базой данных

    @Mock
    private PreparedStatement preparedStatement;  // Мокаем PreparedStatement

    @InjectMocks
    private BDForMailingList databaseManager;  // Подключаем моки к DatabaseManager

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
        String category = "Test category";

        databaseManager.insertUser(userId, category);

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, category);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testInsertLikedNew_SQLException() throws SQLException {
        long userId = 123456L;
        String category = "Test category";

        doThrow(new SQLException("Database error")).when(preparedStatement).executeUpdate();

        databaseManager.insertUser(userId, category);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, category);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteLikedNew() throws SQLException {
        long userId = 123456L;
        String category = "Test category";

        databaseManager.deleteUser(userId, category);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, category);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteLikedNew_SQLException() throws SQLException {
        long userId = 123L;
        String category = "Test category";

        doThrow(new SQLException("Ошибка при удалении")).when(preparedStatement).executeUpdate();

        databaseManager.deleteUser(userId, category);

        verify(preparedStatement, times(1)).setLong(1, userId);
        verify(preparedStatement, times(1)).setString(2, category);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSelectMailingList_Success() throws SQLException {
        long userId = 123456L;
        String category = "Test category";

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Один результат
        when(resultSet.getString("userid")).thenReturn(String.valueOf(userId)); // userid как строка
        when(resultSet.getString("category")).thenReturn(category);

        // Настраиваем поведение мока для PreparedStatement
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<AbstractMap.SimpleEntry<String, String>> data = databaseManager.selectMailingList();

        // Проверяем, что результат содержит ожидаемую новость
        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals(String.valueOf(userId), data.getFirst().getKey()); // Сравниваем как строку
        Assertions.assertEquals(category, data.getFirst().getValue());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectNews_NoResults() throws SQLException {

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false); // Нет результатов

        // Настраиваем поведение мока для PreparedStatement
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<AbstractMap.SimpleEntry<String, String>> data = databaseManager.selectMailingList();

        // Проверяем, что результат пустой
        Assertions.assertTrue(data.isEmpty());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectNews_SQLException() throws SQLException {
        // Настраиваем поведение мока для PreparedStatement
        doThrow(new SQLException("Database error")).when(preparedStatement).executeQuery();

        List<AbstractMap.SimpleEntry<String, String>> likedNews = databaseManager.selectMailingList();

        // Проверяем, что результат пустой при возникновении исключения
        Assertions.assertTrue(likedNews.isEmpty());

        // Убедимся, что методы были вызваны с правильными параметрами
        verify(preparedStatement, times(1)).executeQuery();
    }
}

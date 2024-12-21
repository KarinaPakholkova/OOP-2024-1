package project.database;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

public class RatingDataBaseTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private RatingDataBase ratingDataBase;

    @BeforeAll
    public static void setUpBeforeClass() {
        mockStatic(DriverManager.class);
    }

    @AfterAll
    public static void tearDownAfterClass() {
        Mockito.clearAllCaches(); //
    }

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);
    }

    @Test
    public void testInsertRating() throws SQLException {
        String headline = "example";
        String url = "http://example.com";
        int rating = 5;

        ratingDataBase.insertRating(headline, url, rating);

        verify(preparedStatement, times(1)).setString(1, headline);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).setInt(3, rating);
        verify(preparedStatement, times(1)).setInt(4, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testInsertRating_SQLException() throws SQLException {
        String headline = "example";
        String url = "http://example.com";
        int rating = 5;

        doThrow(new SQLException("Ошибка при добавлении")).when(preparedStatement).executeUpdate();

        ratingDataBase.insertRating(headline, url, rating);

        verify(preparedStatement, times(1)).setString(1, headline);
        verify(preparedStatement, times(1)).setString(2, url);
        verify(preparedStatement, times(1)).setInt(3, rating);
        verify(preparedStatement, times(1)).setInt(4, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSelectRating_Found() throws SQLException {
        String headline = "example";
        String url = "http://example.com";
        int expectedRating = 5;
        int expectedCount = 10;

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true); // Запись найдена
        when(resultSet.getString("headline")).thenReturn(headline);
        when(resultSet.getInt("rating")).thenReturn(expectedRating);
        when(resultSet.getInt("count")).thenReturn(expectedCount);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Object> result = ratingDataBase.selectRating(url);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(headline, result.get(0));
        Assertions.assertEquals(expectedRating, result.get(1));
        Assertions.assertEquals(expectedCount, result.get(2));

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectRating_NotFound() throws SQLException {
        String url = "http://example.com";

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false); // Запись не найдена

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Object> result = ratingDataBase.selectRating(url);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Новость не найдена", result.getFirst());

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectRating_SQLException() throws SQLException {
        String url = "http://example.com";


        doThrow(new SQLException("Ошибка при выполнении запроса")).when(preparedStatement).executeQuery();

        List<Object> result = ratingDataBase.selectRating(url);

        Assertions.assertTrue(result.isEmpty());

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testUpdateRating() throws SQLException {
        String url = "http://example.com";
        int rating = 8;
        int count = 15;

        ratingDataBase.updateRating(url, rating, count);

        verify(preparedStatement, times(1)).setInt(1, rating);
        verify(preparedStatement, times(1)).setInt(2, count);
        verify(preparedStatement, times(1)).setString(3, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateRating_SQLException() throws SQLException {
        String url = "http://example.com";
        int rating = 8;
        int count = 15;

        doThrow(new SQLException("Ошибка при обновлении")).when(preparedStatement).executeUpdate();

        ratingDataBase.updateRating(url, rating, count);

        verify(preparedStatement, times(1)).setInt(1, rating);
        verify(preparedStatement, times(1)).setInt(2, count);
        verify(preparedStatement, times(1)).setString(3, url);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetHighestRatedUrls_Top5() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        // Мокаем 6 новостей
        when(resultSet.next())
                .thenReturn(true, true, true, true, true, true, false);
        when(resultSet.getString("headline"))
                .thenReturn("example1", "example2", "example3", "example4", "example5", "example6");
        when(resultSet.getString("url"))
                .thenReturn("http://example1.com", "http://example2.com", "http://example3.com", "http://example4.com", "http://example5.com", "http://example6.com");
        when(resultSet.getInt("rating"))
                .thenReturn(5, 4, 4, 3, 2, 3);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<String> result = ratingDataBase.getHighestRatedUrls();

        Assertions.assertEquals(6, result.size());
        Assertions.assertEquals("example1 - http://example1.com - Rating: 5", result.get(0));
        Assertions.assertEquals("example2 - http://example2.com - Rating: 4", result.get(1));
        Assertions.assertEquals("example3 - http://example3.com - Rating: 4", result.get(2));
        Assertions.assertEquals("example4 - http://example4.com - Rating: 3", result.get(3));
        Assertions.assertEquals("example6 - http://example6.com - Rating: 3", result.get(5));

        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testGetHighestRatedUrls_LessThan5() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        // Мокаем меньше 5 новостей
        when(resultSet.next())
                .thenReturn(true, true, true, false);
        when(resultSet.getString("headline"))
                .thenReturn("example1", "example2", "example3");
        when(resultSet.getString("url"))
                .thenReturn("http://example1.com", "http://example2.com", "http://example3.com");
        when(resultSet.getInt("rating"))
                .thenReturn(5, 4, 3);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<String> result = ratingDataBase.getHighestRatedUrls();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("example1 - http://example1.com - Rating: 5", result.get(0));
        Assertions.assertEquals("example2 - http://example2.com - Rating: 4", result.get(1));
        Assertions.assertEquals("example3 - http://example3.com - Rating: 3", result.get(2));

        verify(preparedStatement, times(1)).executeQuery();
    }


    @Test
    public void testGetHighestRatedUrls_EmptyResult() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.next()).thenReturn(false);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<String> result = ratingDataBase.getHighestRatedUrls();

        Assertions.assertEquals("Пока что никто ничего не оценил", result.getFirst());

        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testGetHighestRatedUrls_SQLException() throws SQLException {
        doThrow(new SQLException("Ошибка при выполнении запроса")).when(preparedStatement).executeQuery();

        List<String> result = ratingDataBase.getHighestRatedUrls();

        Assertions.assertEquals("Произошла ошибка. Повторите попытку позже", result.getFirst());

        verify(preparedStatement, times(1)).executeQuery();
    }
}

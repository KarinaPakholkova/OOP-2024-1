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
        String url = "http://example.com";
        int rating = 5;

        ratingDataBase.insertRating(url, rating);

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).setInt(2, rating);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testInsertRating_SQLException() throws SQLException {
        String url = "http://example.com";
        int rating = 5;

        doThrow(new SQLException("Ошибка при добавлении")).when(preparedStatement).executeUpdate();

        ratingDataBase.insertRating(url, rating);

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).setInt(2, rating);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSelectRating_Found() throws SQLException {
        String url = "http://example.com";
        int expectedRating = 5;
        int expectedCount = 10;

        // Настраиваем моки для ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true); // Запись найдена
        when(resultSet.getInt("rating")).thenReturn(expectedRating);
        when(resultSet.getInt("count")).thenReturn(expectedCount);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Integer> result = ratingDataBase.selectRating(url);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expectedRating, result.get(0));
        Assertions.assertEquals(expectedCount, result.get(1));

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

        List<Integer> result = ratingDataBase.selectRating(url);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(-1, result.getFirst());

        verify(preparedStatement, times(1)).setString(1, url);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testSelectRating_SQLException() throws SQLException {
        String url = "http://example.com";

        doThrow(new SQLException("Ошибка при выполнении запроса")).when(preparedStatement).executeQuery();

        List<Integer> result = ratingDataBase.selectRating(url);

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
}
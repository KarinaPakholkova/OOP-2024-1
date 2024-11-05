package project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ApiTest {

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse response;

    @InjectMocks
    private Api api;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchLatestNews_Success() throws Exception {
        String json = "{ \"articles\": [ " +
                "{ \"title\": \"Washington Post owner defends decision to end presidential endorsements - Al Jazeera English\", \"url\": \"https://www.aljazeera.com/news/2024/10/29/washington-post-owner-defends-decision-to-end-presidential-endorsements\" }, " +
                "{ \"title\": \"World Series 2024: Freddie Freeman, Walker Buehler lead Dodgers past Yankees for commanding 3-0 lead - Yahoo Sports\", \"url\": \"https://sports.yahoo.com/live/world-series-2024-freddie-freeman-walker-buehler-lead-dodgers-past-yankees-for-commanding-3-0-lead-230010724.html\" } " +
                "{ \"title\": \"Monday Night Football: Steelers get a team win, beat Giants 26-18 - NBC Sports\", \"url\": \"https://www.nbcsports.com/nfl/profootballtalk/rumor-mill/news/monday-night-football-steelers-get-a-team-win-beat-giants-26-18\" } " +
                "{ \"title\": \"Israel has banned the UN agency for Palestinian refugees. That could be devastating for millions - CNN\", \"url\": \"https://www.cnn.com/2024/10/28/middleeast/unrwa-israel-knesset-vote-ban-palestinians-intl/index.html\" } " +
                "{ \"title\": \"Confederate anthem ‘Dixie’ played at Trump’s Madison Square Garden rally - The Washington Post\", \"url\": \"https://www.washingtonpost.com/nation/2024/10/28/trump-madison-square-garden-rally-dixie-song-controversy/\" } " +
                "] }";
        StringEntity entity = new StringEntity(json);

        // Настраиваем поведение response
        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);

        String result = api.fetchLatestNews();

        assertTrue(result.contains("1. Washington Post owner defends decision to end presidential endorsements - Al Jazeera English"));
        assertTrue(result.contains("Подробнее: https://www.aljazeera.com/news/2024/10/29/washington-post-owner-defends-decision-to-end-presidential-endorsements"));
        assertTrue(result.contains("2. World Series 2024: Freddie Freeman, Walker Buehler lead Dodgers past Yankees for commanding 3-0 lead - Yahoo Sports"));
        assertTrue(result.contains("Подробнее: https://sports.yahoo.com/live/world-series-2024-freddie-freeman-walker-buehler-lead-dodgers-past-yankees-for-commanding-3-0-lead-230010724.html"));
        assertTrue(result.contains("3. Monday Night Football: Steelers get a team win, beat Giants 26-18 - NBC Sports"));
        assertTrue(result.contains("Подробнее: https://www.nbcsports.com/nfl/profootballtalk/rumor-mill/news/monday-night-football-steelers-get-a-team-win-beat-giants-26-18"));
        assertTrue(result.contains("4. Israel has banned the UN agency for Palestinian refugees. That could be devastating for millions - CNN"));
        assertTrue(result.contains("Подробнее: https://www.cnn.com/2024/10/28/middleeast/unrwa-israel-knesset-vote-ban-palestinians-intl/index.html"));
        assertTrue(result.contains("5. Confederate anthem ‘Dixie’ played at Trump’s Madison Square Garden rally - The Washington Post"));
        assertTrue(result.contains("Подробнее: https://www.washingtonpost.com/nation/2024/10/28/trump-madison-square-garden-rally-dixie-song-controversy/"));
    }

    @Test
    public void testParseNewsResponse_EmptyArticles() {
        String json = "{ \"articles\": [] }";
        String result = api.parseNewsResponse(json);
        assertEquals("Не удалось получить новости.", result);
    }

    @Test
    public void testParseNewsResponse_InvalidJson() {
        String json = "invalid json";
        String result = api.parseNewsResponse(json);
        assertEquals("Ошибка при обработке новостей.", result);
    }
}
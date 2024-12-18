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
import project.API.Api;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;

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
                "{ \"title\": \"Jalen Brunson’s game-winner, Mikal Bridges’ block helps Knicks escape with rivalry win over Nets - New York Post\", \"url\": \"https://nypost.com/2024/11/15/sports/jalen-brunsons-game-winner-helps-knicks-escape-with-win-over-nets/\" }, " +
                "{ \"title\": \"Israel drops massive bomb in Beirut strike, as Lebanon mulls cease-fire - The Washington Post\", \"url\": \"https://www.washingtonpost.com/world/2024/11/15/beirut-strike-israel-lebanon-ceasefire/\" }, " +
                "{ \"title\": \"Global health experts sound alarm over RFK Jr., citing Samoa outbreak - The Washington Post\", \"url\": \"https://www.washingtonpost.com/world/2024/11/15/rfk-jr-global-health-samoa-kennedy/\" }, " +
                "{ \"title\": \"Taylor edges out Serrano in controversial decision - ESPN\", \"url\": \"https://www.espn.com/boxing/story/_/id/42417518/katie-taylor-edges-amanda-serrano-controversial-decision\" }, " +
                "{ \"title\": \"Nuggets star Nikola Jokic and coach Michael Malone miss game against Pelicans for personal reasons - The Associated Press\", \"url\": \"https://apnews.com/article/nuggets-jokic-malone-356652a81e208ae9647384cc9496ddb6\" } " +
                "] }";
        StringEntity entity = new StringEntity(json);

        // Настраиваем поведение response
        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);

        // Вызываем метод fetchLatestNews()
        List<AbstractMap.SimpleEntry<String, String>> result = api.fetchLatestNews();

        // Проверяем размер листа
        assertEquals(5, result.size());

        // Сравниваем результаты
        assertTrue(result.get(0).getKey().contains("Jalen Brunson’s game-winner, Mikal Bridges’ block helps Knicks escape with rivalry win over Nets - New York Post"));
        assertEquals("https://nypost.com/2024/11/15/sports/jalen-brunsons-game-winner-helps-knicks-escape-with-win-over-nets/", result.get(0).getValue());

        assertTrue(result.get(1).getKey().contains("Israel drops massive bomb in Beirut strike, as Lebanon mulls cease-fire - The Washington Post"));
        assertEquals("https://www.washingtonpost.com/world/2024/11/15/beirut-strike-israel-lebanon-ceasefire/", result.get(1).getValue());

        assertTrue(result.get(2).getKey().contains("Global health experts sound alarm over RFK Jr., citing Samoa outbreak - The Washington Post"));
        assertEquals("https://www.washingtonpost.com/world/2024/11/15/rfk-jr-global-health-samoa-kennedy/", result.get(2).getValue());

        assertTrue(result.get(3).getKey().contains("Taylor edges out Serrano in controversial decision - ESPN"));
        assertEquals("https://www.espn.com/boxing/story/_/id/42417518/katie-taylor-edges-amanda-serrano-controversial-decision", result.get(3).getValue());

        assertTrue(result.get(4).getKey().contains("Nuggets star Nikola Jokic and coach Michael Malone miss game against Pelicans for personal reasons - The Associated Press"));
        assertEquals("https://apnews.com/article/nuggets-jokic-malone-356652a81e208ae9647384cc9496ddb6", result.get(4).getValue());
    }

    @Test
    public void testParseNewsResponse_EmptyArticles() {
        // Проверка на пустой лист
        String json = "{ \"articles\": [] }";
        List<AbstractMap.SimpleEntry<String, String>> result = api.parseNewsResponse(json);
        assertEquals(1, result.size());
        assertEquals("Не удалось получить новости.", result.get(0).getKey());
        assertEquals("", result.get(0).getValue());
    }

    @Test
    public void testParseNewsResponse_InvalidJson() {
        // Проверка на инвалида
        String json = "invalid json";
        List<AbstractMap.SimpleEntry<String, String>> result = api.parseNewsResponse(json);
        assertEquals(1, result.size());
        assertEquals("Ошибка при обработке новостей.", result.get(0).getKey());
        assertEquals("", result.get(0).getValue());
    }

    @Test
    public void testFetchNewsCategory() throws IOException {
        String json = "{ \"articles\": [ " +
                "{ \"title\": \"Nintendo Expands Switch Online's NES Library With Another Classic Next Week - Nintendo Life\", \"url\": \"https://www.nintendolife.com/news/2024/12/nintendo-expands-switch-onlines-nes-library-with-another-classic-next-week\" }, " +
                "{ \"title\": \"My Millennial Mind Is On Fire After Learning That These Items Were Staples In Most Pre-1980 Homes - BuzzFeed\", \"url\": \"https://www.buzzfeed.com/rossyoder/popular-household-items-before-1980-fs\" }, " +
                "{ \"title\": \"The 40 Best Cyber Monday Laptop Deals - WIRED\", \"url\": \"https://www.wired.com/story/best-cyber-monday-laptop-deals-2024/\" }, " +
                "{ \"title\": \"What’s new in Android’s December 2024 Google System Updates - 9to5Google\", \"url\": \"http://9to5google.com/2024/12/02/december-2024-google-system-updates/\" }, " +
                "{ \"title\": \"New PlayStation handheld report backed up by Digital Foundry - Video Games Chronicle\", \"url\": \"https://www.videogameschronicle.com/news/new-playstation-handheld-report-backed-up-by-digital-foundry/\" }, " +
                "] }";
        StringEntity entity = new StringEntity(json);

        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);

        List<AbstractMap.SimpleEntry<String, String>> result = api.fetchNewsCategory("technology");

        assertEquals(5, result.size());

        assertTrue(result.get(0).getKey().contains("Nintendo Expands Switch Online's NES Library With Another Classic Next Week - Nintendo Life"));
        assertEquals("https://www.nintendolife.com/news/2024/12/nintendo-expands-switch-onlines-nes-library-with-another-classic-next-week", result.get(0).getValue());

        assertTrue(result.get(1).getKey().contains("My Millennial Mind Is On Fire After Learning That These Items Were Staples In Most Pre-1980 Homes - BuzzFeed"));
        assertEquals("https://www.buzzfeed.com/rossyoder/popular-household-items-before-1980-fs", result.get(1).getValue());

        assertTrue(result.get(2).getKey().contains("The 40 Best Cyber Monday Laptop Deals - WIRED"));
        assertEquals("https://www.wired.com/story/best-cyber-monday-laptop-deals-2024/", result.get(2).getValue());

        assertTrue(result.get(3).getKey().contains("What’s new in Android’s December 2024 Google System Updates - 9to5Google"));
        assertEquals("http://9to5google.com/2024/12/02/december-2024-google-system-updates/", result.get(3).getValue());

        assertTrue(result.get(4).getKey().contains("New PlayStation handheld report backed up by Digital Foundry - Video Games Chronicle"));
        assertEquals("https://www.videogameschronicle.com/news/new-playstation-handheld-report-backed-up-by-digital-foundry/", result.get(4).getValue());
    }
}

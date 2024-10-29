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
                "{ \"title\": \"[Removed]\", \"url\": \"https://removed.com\" }, " +
                "{ \"title\": \"[Removed]\", \"url\": \"https://removed.com\" } " +
                "{ \"title\": \"Apple Music helps artists turn concert set lists into playlists\", \"url\": \"https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_7ea2282d-108d-445c-b4b9-8c1fd169551d\" } " +
                "{ \"title\": \"The Apple Pencil Pro is 30 percent off, taking it down to an all-time low price\", \"url\": \"https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_2711bbdf-0325-4496-a71b-8ef9816078e3\" } " +
                "{ \"title\": \"iOS 18.1 launches next week with Apple Intelligence and AirPods Pro hearing tests and aids\", \"url\": \"https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_53f156c9-a116-4dee-b151-0e8462185bfe\" } " +
                "] }";
        StringEntity entity = new StringEntity(json);

        // Настраиваем поведение response
        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);

        String result = api.fetchLatestNews();

        assertTrue(result.contains("1. [Removed]"));
        assertTrue(result.contains("Подробнее: https://removed.com"));
        assertTrue(result.contains("2. [Removed]"));
        assertTrue(result.contains("Подробнее: https://removed.com"));
        assertTrue(result.contains("3. Apple Music helps artists turn concert set lists into playlists"));
        assertTrue(result.contains("Подробнее: https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_7ea2282d-108d-445c-b4b9-8c1fd169551d"));
        assertTrue(result.contains("4. The Apple Pencil Pro is 30 percent off, taking it down to an all-time low price"));
        assertTrue(result.contains("Подробнее: https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_2711bbdf-0325-4496-a71b-8ef9816078e3"));
        assertTrue(result.contains("5. iOS 18.1 launches next week with Apple Intelligence and AirPods Pro hearing tests and aids"));
        assertTrue(result.contains("Подробнее: https://consent.yahoo.com/v2/collectConsent?sessionId=1_cc-session_53f156c9-a116-4dee-b151-0e8462185bfe"));
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
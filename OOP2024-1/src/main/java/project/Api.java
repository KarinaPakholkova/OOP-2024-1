package project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Api {

    public String fetchLatestNews() {
        String apiKey = System.getenv("NEWS_API_KEY");
        String apiUrl = "https://newsapi.org/v2/everything?q=Apple&from=2024-10-13&sortBy=popularity&apiKey=" + apiKey;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseNewsResponse(jsonResponse);
                } else {
                    return "Не удалось получить новости. Статус: " + response.getCode();
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return "Ошибка при получении новостей.";
        }
    }// Метод для парсинга JSON-ответа и формирования строки с заголовками новостей

    private String parseNewsResponse(String jsonResponse) {
        StringBuilder newsBuilder = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode articlesNode = rootNode.path("articles");

            if (articlesNode.isArray()) {
                for (int i = 0; i < Math.min(5, articlesNode.size()); i++) {
                    JsonNode article = articlesNode.get(i);
                    String title = article.path("title").asText();
                    String url = article.path("url").asText();
                    newsBuilder.append(i + 1).append(". ").append(title).append("\n");
                    newsBuilder.append("Подробнее: ").append(url).append("\n\n");
                }
            } else {
                newsBuilder.append("Не удалось получить новости.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при обработке новостей.";
        }
        return newsBuilder.toString();
    }
}

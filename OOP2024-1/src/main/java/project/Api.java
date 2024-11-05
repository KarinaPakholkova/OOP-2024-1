package project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Array;

public class Api {

    public String fetchLatestNews() {
        String apiKey = System.getenv("NEWS_API_KEY");
        String apiUrl = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + apiKey;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseNewsResponse(jsonResponse);
                } else {
                    return "Не удалось получить новости. Статус: " + response.getStatusLine().getStatusCode();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при получении новостей.";
        }
    }

    // Метод для парсинга JSON-ответа и формирования строки с заголовками новостей
    String parseNewsResponse(String jsonResponse) {
        StringBuilder newsBuilder = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode articlesNode = rootNode.path("articles");
            int k = 0;
            if (articlesNode.isArray() && !(articlesNode.isEmpty())) {
                for (int i = 0; i < articlesNode.size() && k != 5; i++) {
                    JsonNode article = articlesNode.get(i);
                    String title = article.path("title").asText();
                    String url = article.path("url").asText();
                    if (url.equals("https://removed.com")) {
                        continue;
                    }
                    newsBuilder.append(i + 1).append(". ").append(title).append("\n");
                    newsBuilder.append("Подробнее: ").append(url).append("\n\n");
                    k++;
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
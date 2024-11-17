package project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.AbstractMap.SimpleEntry; // Импортируем SimpleEntry

public class Api {

    public List<SimpleEntry<String, String>> fetchLatestNews() {
        String apiKey = System.getenv("NEWS_API_KEY");
        String apiUrl = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + apiKey;
        List<SimpleEntry<String, String>> newsList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    newsList = parseNewsResponse(jsonResponse);
                } else {
                    newsList.add(new SimpleEntry<>("Не удалось получить новости. Статус: " + response.getStatusLine().getStatusCode(), ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            newsList.add(new SimpleEntry<>("Ошибка при получении новостей.", ""));
        }
        return newsList;
    }

    List<SimpleEntry<String, String>> parseNewsResponse(String jsonResponse) {
        List<SimpleEntry<String, String>> newsList = new ArrayList<>();
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
                    newsList.add(new SimpleEntry<>(title, url));
                    k++;
                }
            }
            else {
                newsList.add(new SimpleEntry<>("Не удалось получить новости.", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
            newsList.add(new SimpleEntry<>("Ошибка при обработке новостей.", ""));
        }
        return newsList;
    }
}

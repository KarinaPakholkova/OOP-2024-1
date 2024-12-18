package project.auxiliaryFunctions;

import project.API.Api;

import java.util.AbstractMap;
import java.util.List;

public class CreateString {
    Api apiCategories = new Api();

    public StringBuilder printCategoryNews(String category){
        StringBuilder categoryNewsText = new StringBuilder();
        List<AbstractMap.SimpleEntry<String, String>> categoryNewsList = apiCategories.fetchNewsCategory(category);
        categoryNewsText.append("Новости категории '").append(category).append("':\n");
        for (int i = 0; i < categoryNewsList.size(); i++) {
            AbstractMap.SimpleEntry<String, String> news = categoryNewsList.get(i);
            categoryNewsText.append(i + 1).append(". ").append(news.getKey()).append("\n").append(news.getValue()).append("\n");
        }
        return categoryNewsText;
    }
}

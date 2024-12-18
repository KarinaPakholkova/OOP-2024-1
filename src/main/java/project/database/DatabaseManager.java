package project.database;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    DataBaseConnection DBConnect = new DataBaseConnection();

    // Метод для добавления новой записи в таблицу
    public void insertLikedNew(Long userId, String headlines, String url) {
        DBConnect.connect();
        String sql = "INSERT INTO users (userID, headline, url) VALUES (?, ?, ?)";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, headlines);
            prSt.setString(3, url);
            prSt.executeUpdate();
            System.out.println("Запись успешно добавлена в таблицу users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении записи: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    // Метод удаления записи
    public void deleteLikedNew(Long userId, String url) {
        DBConnect.connect();
        String sql = "DELETE FROM users WHERE userid = ? AND url = ?";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, url);
            prSt.executeUpdate();
            System.out.println("Запись успешно удалена из таблицы users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    // метод для вывода сохраненных новостей
    public List<AbstractMap.SimpleEntry<String, String>> selectNews(Long userId) {
        DBConnect.connect();
        List<AbstractMap.SimpleEntry<String, String>> likedNews = new ArrayList<>();
        String sql = "SELECT headline, url FROM users WHERE userID = ?";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            ResultSet resultSet = prSt.executeQuery();
            while (resultSet.next()) {
                String headline = resultSet.getString("headline");
                String url = resultSet.getString("url");
                likedNews.add(new AbstractMap.SimpleEntry<>(headline, url));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении сохраненных новостей: " + e.getMessage());
        }
        DBConnect.disconnect();
        return likedNews;
    }
}



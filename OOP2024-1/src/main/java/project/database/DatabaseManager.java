package project.database;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Connection connection;

    // Метод для подключения к базе данных
    public void connect() {
        try {
            String url = "jdbc:postgresql://localhost:5432/dbfortgbot";
            String user = System.getenv("DATABASE_USER");
            String password = System.getenv("DATABASE_PASSWORD");
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Connected to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    // Метод для добавления новой записи в таблицу
    public void insertLikedNew(Long userId, String headlines, String url) {
        connect();
        String sql = "INSERT INTO users (userID, headline, url) VALUES (?, ?, ?)";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, headlines);
            prSt.setString(3, url);
            prSt.executeUpdate();
            System.out.println("Запись успешно добавлена в таблицу users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении записи: " + e.getMessage());
        }
        disconnect();
    }

    // Метод удаления записи
    public void deleteLikedNew(Long userId, String url) {
        connect();
        String sql = "DELETE FROM users WHERE userid = ? AND url = ?";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, url);
            prSt.executeUpdate();
            System.out.println("Запись успешно удалена из таблицы users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи: " + e.getMessage());
        }
        disconnect();
    }

    // метод для вывода сохраненных новостей
    public List<AbstractMap.SimpleEntry<String, String>> selectNews(Long userId) {
        connect();
        List<AbstractMap.SimpleEntry<String, String>> likedNews = new ArrayList<>();
        String sql = "SELECT headline, url FROM users WHERE userID = ?";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
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
        disconnect();
        return likedNews;
    }

        // Метод для закрытия соединения с базой данных
    public void disconnect () {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}


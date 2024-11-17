package project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    // Метод для подключения к базе данных
    public void connect() {
        try {
            // Замените URL, USER и PASSWORD на ваши данные
            String url = "jdbc:postgresql://localhost:5432/dbfortgbot";
            String user = "postgres";
            String password = "qwerkiller";
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

    // Метод для закрытия соединения с базой данных
    public void disconnect() {
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

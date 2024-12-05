package project.database;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class BDForMailingList {
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
    public void insertUser(Long userId, String category) {
        connect();
        String sql = "INSERT INTO mailing_list (userID, category) VALUES (?, ?)";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, category);
            prSt.executeUpdate();
            System.out.println("Запись успешно добавлена в таблицу mailing_list.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении записи: " + e.getMessage());
        }
        disconnect();
    }

    // Метод удаления записи
    public void deleteUser(Long userId, String category) {
        connect();
        String sql = "DELETE FROM mailing_list WHERE userid = ? AND category = ?";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, category);
            prSt.executeUpdate();
            System.out.println("Запись успешно удалена из таблицы users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи: " + e.getMessage());
        }
        disconnect();
    }

    public List<AbstractMap.SimpleEntry<String, String>> selectMailingList() {
        connect();
        List<AbstractMap.SimpleEntry<String, String>> data = new ArrayList<>();
        String sql = "SELECT userid, category FROM mailing_list";

        try (PreparedStatement prSt = connection.prepareStatement(sql)) {
            ResultSet resultSet = prSt.executeQuery();
            while (resultSet.next()) {
                String chatId = resultSet.getString("userid");
                String category = resultSet.getString("category");
                data.add(new AbstractMap.SimpleEntry<>(chatId, category));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении записей: " + e.getMessage());
        }
        disconnect();
        return data;
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


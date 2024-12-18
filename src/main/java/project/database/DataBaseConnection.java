package project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
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

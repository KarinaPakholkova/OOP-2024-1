package project.database;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class BDForMailingList {
    DataBaseConnection DBConnect = new DataBaseConnection();

    // Метод для добавления новой записи в таблицу
    public void insertUser(Long userId, String category) {
        DBConnect.connect();
        String sql = "INSERT INTO mailing_list (userID, category) VALUES (?, ?)";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, category);
            prSt.executeUpdate();
            System.out.println("Запись успешно добавлена в таблицу mailing_list.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении записи: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    // Метод удаления записи
    public void deleteUser(Long userId, String category) {
        DBConnect.connect();
        String sql = "DELETE FROM mailing_list WHERE userid = ? AND category = ?";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setLong(1, userId);
            prSt.setString(2, category);
            prSt.executeUpdate();
            System.out.println("Запись успешно удалена из таблицы users.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    public List<AbstractMap.SimpleEntry<String, String>> selectMailingList() {
        DBConnect.connect();
        List<AbstractMap.SimpleEntry<String, String>> data = new ArrayList<>();
        String sql = "SELECT userid, category FROM mailing_list";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            ResultSet resultSet = prSt.executeQuery();
            while (resultSet.next()) {
                String chatId = resultSet.getString("userid");
                String category = resultSet.getString("category");
                data.add(new AbstractMap.SimpleEntry<>(chatId, category));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении записей: " + e.getMessage());
        }
        DBConnect.disconnect();
        return data;
    }
}


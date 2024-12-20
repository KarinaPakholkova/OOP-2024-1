package project.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDataBase {
    DataBaseConnection DBConnect = new DataBaseConnection();

    // Метод для добавления нового рейтинга для новости
    public void insertRating(String url, int rating) {
        DBConnect.connect();
        String sql = "INSERT INTO rating (url, rating, count) VALUES (?, ?, ?)";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setString(1, url);
            prSt.setInt(2, rating);
            prSt.setInt(3, 1);
            prSt.executeUpdate();
            System.out.println("Оценка успешно добавлена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении оценки: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    // селектор рейтинга
    public List<Integer> selectRating(String url) {
        List<Integer> resultList = new ArrayList<>();
        DBConnect.connect();
        String sql = "SELECT rating, count FROM rating WHERE url = ?;";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setString(1, url);
            ResultSet resultSet = prSt.executeQuery();

            if (resultSet.next()) {
                int rating = resultSet.getInt("rating");
                int count = resultSet.getInt("count");
                resultList.add(rating);
                resultList.add(count);
            } else {
                resultList.add(-1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении оценки: " + e.getMessage());
        }
        DBConnect.disconnect();
        return resultList;
    }

    public void updateRating(String url, int rating, int count) {
        DBConnect.connect();
        String sql = """
                UPDATE rating
                SET rating = ?, count = ?
                WHERE url = ?;""";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setInt(1, rating);
            prSt.setInt(2, count);
            prSt.setString(3, url);
            prSt.executeUpdate();
            System.out.println("Оценка успешно обновлена");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении оценки: " + e.getMessage());
        }
        DBConnect.disconnect();
    }
}

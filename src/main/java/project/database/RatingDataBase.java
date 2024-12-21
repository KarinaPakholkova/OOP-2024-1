package project.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDataBase {
    DataBaseConnection DBConnect = new DataBaseConnection();

    // Метод для добавления нового рейтинга для новости
    public void insertRating(String headline, String url, int rating) {
        DBConnect.connect();
        String sql = "INSERT INTO rating (headline, url, rating, count) VALUES (?, ?, ?, ?)";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setString(1, headline);
            prSt.setString(2, url);
            prSt.setInt(3, rating);
            prSt.setInt(4, 1);
            prSt.executeUpdate();
            System.out.println("Оценка успешно добавлена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении оценки: " + e.getMessage());
        }
        DBConnect.disconnect();
    }

    // селектор рейтинга
    public List<Object> selectRating(String url) {
        List<Object> resultList = new ArrayList<>();
        DBConnect.connect();
        String sql = "SELECT headline, rating, count FROM rating WHERE url = ?;";

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            prSt.setString(1, url);
            ResultSet resultSet = prSt.executeQuery();

            if (resultSet.next()) {
                String headline = resultSet.getString("headline");
                int rating = resultSet.getInt("rating");
                int count = resultSet.getInt("count");
                resultList.add(headline);
                resultList.add(rating);
                resultList.add(count);
            } else {
                resultList.add("Новость не найдена");
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

    public List<String> getHighestRatedUrls() {
        List<String> highestRatedUrls = new ArrayList<>();
        DBConnect.connect();

        String sql = """
            SELECT headline, url, rating
            FROM rating
            ORDER BY rating DESC
            LIMIT 5;
            """;

        try (PreparedStatement prSt = DBConnect.connection.prepareStatement(sql)) {
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                String headline = resultSet.getString("headline");
                String url = resultSet.getString("url");
                int rating = resultSet.getInt("rating");
                highestRatedUrls.add(headline + " - " + url + " - Rating: " + rating);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении ссылок с самым высоким рейтингом: " + e.getMessage());
            highestRatedUrls.add("Произошла ошибка. Повторите попытку позже");
        }
        DBConnect.disconnect();
        if (highestRatedUrls.isEmpty())
        {
            highestRatedUrls.add("Пока что никто ничего не оценил");
        }
        return highestRatedUrls;
    }
}

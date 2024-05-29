package org.example.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PostgresDB {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/kursach2";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    public void savePost(String channelUsername, String postText, int post_id) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO posts (channel_username, post_text, post_id) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, channelUsername);
                preparedStatement.setString(2, postText);
                preparedStatement.setInt(3, post_id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllPosts() {
        List<String> posts = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT post_id, post_text FROM posts";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                String postText = resultSet.getString("post_text");
                String postLink = "https://t.me/kursachBott/" + postId;
                String formattedPost = postLink + "\n" + "Пост: " + postText;
                posts.add(formattedPost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public void saveComment(int postId, String commenterName, String commentText) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO comments (post_id, commenter_name, comment_text) VALUES (?, ?, ?)")) {
            statement.setInt(1, postId);
            statement.setString(2, commenterName);
            statement.setString(3, commentText);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllComments(int postId) throws SQLException {
        List<String> comments = new ArrayList<>();
        String query = "SELECT commenter_name, comment_text FROM comments WHERE post_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String commenterName = resultSet.getString("commenter_name");
                    String commentText = resultSet.getString("comment_text");
                    String formattedComment = commenterName + "\n" + "Оставил(-а) комментарий:\n " + commentText;
                    comments.add(formattedComment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return comments;
    }

    public String getMostPopularPost() {
        String mostPopularPost = "";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT p.post_id, p.post_text, COUNT(c.id) AS comment_count " +
                    "FROM posts p " +
                    "LEFT JOIN comments c ON p.post_id = c.post_id " +
                    "GROUP BY p.post_id, p.post_text " +
                    "ORDER BY comment_count DESC " +
                    "LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                String postText = resultSet.getString("post_text");
                int commentCount = resultSet.getInt("comment_count");
                String postLink = "https://t.me/kursachBott/" + postId;
                mostPopularPost = postLink + "\n" +
                        "Текст поста: " + postText + "\n" +
                        "Количество комментариев: " + commentCount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mostPopularPost;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

}



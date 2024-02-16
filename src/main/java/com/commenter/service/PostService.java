package com.commenter.service;

import com.commenter.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostService {
  public List<Post> getAllPosts() {
    try (Connection connection = DatabaseService.getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT * FROM POSTS");
         ResultSet resultSet = statement.executeQuery()) {

        List<Post> posts = new ArrayList<>();

        while (resultSet.next()) {
          Post post = Post.builder()
              .id(resultSet.getInt("id"))
              .title(resultSet.getString("title"))
              .content(resultSet.getString("content"))
              .user(resultSet.getString("user"))
              .build();

          posts.add(post);
        }
        return posts;
    } catch (SQLException e) {
      throw new RuntimeException("Failed getting all post");
    }
  }
}

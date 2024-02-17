package com.commenter.service;

import com.commenter.model.CreateNewPostRequest;
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

  public Post createNewPost(CreateNewPostRequest request) {
    try (Connection connection = DatabaseService.getConnection();
         PreparedStatement statement = connection.prepareStatement(
             "INSERT INTO posts (\"user\", title, content) VALUES (?, ?, ?) RETURNING id, title, content, \"user\"");
    ) {
      statement.setString(1, request.getUser());
      statement.setString(2, request.getTitle());
      statement.setString(3, request.getContent());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          int id = resultSet.getInt("id");
          String title = resultSet.getString("title");
          String content = resultSet.getString("content");
          String user = resultSet.getString("user");

          return Post.builder()
              .id(id)
              .title(title)
              .content(content)
              .user(user)
              .build();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create a new post", e);
    }
    return null;
  }
}

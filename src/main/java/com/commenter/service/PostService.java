package com.commenter.service;

import com.commenter.model.CreateEditPostRequest;
import com.commenter.model.Post;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;

public class PostService {

  private static final String GET_ALL_POST_QUERY =
      "SELECT * FROM posts";
  private static final String CREATE_NEW_POST_QUERY =
      "INSERT INTO posts (\"user\", title, content) VALUES (?, ?, ?) RETURNING id, title, content, \"user\"";
  private static final String UPDATE_POST_BY_ID_QUERY =
      "UPDATE posts SET title = ?, content = ? WHERE id = ?";
  private static final String DELETE_POST_BY_ID_QUERY =
      "DELETE FROM posts WHERE id = ?";

  private final DatabaseService databaseService;

  @Inject
  public PostService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  public List<Post> getAllPosts() {
    try {
      return databaseService.executeQuery(GET_ALL_POST_QUERY)
          .stream()
          .map(this::toPost)
          .toList();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get all posts", e);
    }
  }

  public Post createNewPost(CreateEditPostRequest request) {
    return databaseService.executeQuery(
            CREATE_NEW_POST_QUERY,
            request.getUser(), request.getTitle(), request.getContent())
        .stream()
        .findFirst()
        .map(this::toPost)
        .orElseThrow(() -> new RuntimeException("Failed to create a new post"));
  }

  public boolean editPostById(int postId, CreateEditPostRequest request) {
    int affectedRows = databaseService.executeUpdate(
        UPDATE_POST_BY_ID_QUERY, request.getTitle(), request.getContent(), postId);
    return affectedRows > 0;
  }

  public boolean deletePostById(int postId) {
    int affectedRows = databaseService.executeUpdate(DELETE_POST_BY_ID_QUERY, postId);
    return affectedRows > 0;
  }

  private Post toPost(Map<String, Object> mp) {
    return Post.builder()
        .id((Integer) mp.get("id"))
        .title((String) mp.get("title"))
        .content((String) mp.get("content"))
        .user((String) mp.get("user"))
        .build();
  }
}

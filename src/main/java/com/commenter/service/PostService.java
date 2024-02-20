package com.commenter.service;

import com.commenter.model.CreateEditPostRequest;
import com.commenter.model.Post;
import com.commenter.model.ResponseVO;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PostService {

  private static final String GET_ALL_POST_QUERY =
      "SELECT * FROM posts";
  private static final String GET_POST_BY_ID_QUERY =
      "SELECT * FROM posts WHERE id = ?";
  private static final String GET_POST_BY_ID_AND_USER_QUERY =
      "SELECT * FROM posts WHERE id = ? AND \"user\" = ?";
  private static final String CREATE_NEW_POST_QUERY =
      "INSERT INTO posts (\"user\", title, content) VALUES (?, ?, ?) RETURNING id, title, content, \"user\"";
  private static final String UPDATE_POST_BY_ID_QUERY =
      "UPDATE posts SET title = ?, content = ? WHERE id = ?";
  private static final String INCREMENT_POST_COMMENT_BY_ID_QUERY =
      "UPDATE posts SET comments = comments + 1 WHERE id = ?";
  private static final String DECREMENT_POST_COMMENT_BY_ID_QUERY =
      "UPDATE posts SET comments = comments - 1 WHERE id = ?";
  private static final String DELETE_POST_BY_ID_QUERY =
      "DELETE FROM posts WHERE id = ?";

  private final DatabaseService databaseService;

  @Inject
  public PostService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  public ResponseVO<List<Post>> getAllPosts() {
    try {
      List<Post> posts = databaseService.executeQuery(GET_ALL_POST_QUERY)
          .stream()
          .map(this::toPost)
          .toList();

      return ResponseVO.<List<Post>>builder().data(posts).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get all posts", e);
    }
  }

  public ResponseVO<Post> getPostById(int postId) {
    try {
      Post post = databaseService.executeQuery(GET_POST_BY_ID_QUERY, postId)
          .stream()
          .findFirst()
          .map(this::toPost)
          .orElse(null);
      return ResponseVO.<Post>builder().data(post).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get post by id", e);
    }
  }

  public ResponseVO<Post> createNewPost(CreateEditPostRequest request) {
    List<String> errors = validateRequest(request);

    if (!errors.isEmpty()) {
      return ResponseVO.<Post>builder()
          .errors(errors)
          .build();
    }

    return databaseService.executeQuery(
            CREATE_NEW_POST_QUERY,
            request.getUser(), request.getTitle(), request.getContent())
        .stream()
        .findFirst()
        .map(this::toPostResponseVO)
        .orElseThrow(() -> new RuntimeException("Failed to create a new post"));
  }

  public ResponseVO<Boolean> editPostById(int postId, CreateEditPostRequest request) {
    List<String> errors = validateRequest(request);

    if (!errors.isEmpty()) {
      return ResponseVO.<Boolean>builder()
          .errors(errors)
          .build();
    }
    try {
      if (databaseService.executeQuery(GET_POST_BY_ID_AND_USER_QUERY, postId, request.getUser()).isEmpty()) {
        return ResponseVO.<Boolean>builder().errors(Collections.singletonList("Post data not found")).build();
      }
      databaseService.executeUpdate(UPDATE_POST_BY_ID_QUERY, request.getTitle(), request.getContent(), postId);
      return ResponseVO.<Boolean>builder().data(Boolean.TRUE).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to edit post");
    }
  }

  public ResponseVO<Boolean> deletePostById(int postId, String user) {
    if (user == null) {
      return ResponseVO.<Boolean>builder().errors(Collections.singletonList("The user field cannot be empty")).build();
    }
    try {
      if (databaseService.executeQuery(GET_POST_BY_ID_AND_USER_QUERY, postId, user).isEmpty()) {
        return ResponseVO.<Boolean>builder().errors(Collections.singletonList("Post data not found")).build();
      }
      databaseService.executeUpdate(DELETE_POST_BY_ID_QUERY, postId);
      return ResponseVO.<Boolean>builder().data(Boolean.TRUE).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete post");
    }
  }

  public void modifyPostCommentById(int postId, boolean isIncrement) {
    try {
      String query = (isIncrement) ? INCREMENT_POST_COMMENT_BY_ID_QUERY : DECREMENT_POST_COMMENT_BY_ID_QUERY;
      databaseService.executeUpdate(query, postId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to modify post comment");
    }
  }

  private Post toPost(Map<String, Object> mp) {
    return Post.builder()
        .id((Integer) mp.get("id"))
        .title((String) mp.get("title"))
        .content((String) mp.get("content"))
        .user((String) mp.get("user"))
        .comments((Integer) mp.get("comments"))
        .build();
  }

  private ResponseVO<Post> toPostResponseVO(Map<String, Object> mp) {
    return ResponseVO.<Post>builder().data(toPost(mp)).build();
  }

  private List<String> validateRequest(CreateEditPostRequest request) {
    List<String> errors = new ArrayList<>();
    if (request.getTitle() == null || request.getTitle().isEmpty()) {
      errors.add("The title field cannot be empty");
    }
    if (request.getUser() == null || request.getUser().isEmpty()) {
      errors.add("The user field cannot be empty");
    }
    if (request.getContent() == null || request.getContent().isEmpty()) {
      errors.add("The content field cannot be empty");
    }
    return errors;
  }
}

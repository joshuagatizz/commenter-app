package com.commenter.service;

import com.commenter.model.Comment;
import com.commenter.model.CreateCommentRequest;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;

public class CommentService {

  private static final String GET_ALL_COMMENTS_BY_POST_ID_QUERY =
      "SELECT * FROM comments WHERE post_id = ?";
  private static final String CREATE_NEW_COMMENT_QUERY =
      "INSERT INTO comments (\"user\", content, post_id) VALUES (?, ?, ?) RETURNING id, \"user\", content, post_id";
  private final DatabaseService databaseService;

  @Inject
  public CommentService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  public List<Comment> getAllCommentsByPostId(int postId) {
    try {
      return databaseService.executeQuery(GET_ALL_COMMENTS_BY_POST_ID_QUERY, postId)
          .stream()
          .map(this::toComment)
          .toList();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get all comments by postId", e);
    }
  }

  public Comment createNewComment(int postId, CreateCommentRequest request) {
    return databaseService.executeQuery(
            CREATE_NEW_COMMENT_QUERY,
            request.getUser(), request.getContent(), postId)
        .stream()
        .findFirst()
        .map(this::toComment)
        .orElseThrow(() -> new RuntimeException("Failed to create a new comment"));
  }

  private Comment toComment(Map<String, Object> mp) {
    return Comment.builder()
        .id((Integer) mp.get("id"))
        .user((String) mp.get("user"))
        .content((String) mp.get("content"))
        .postId((Integer) mp.get("post_id"))
        .build();
  }
}

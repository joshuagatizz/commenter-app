package com.commenter.service;

import com.commenter.model.Comment;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;

public class CommentService {

  private static final String GET_ALL_COMMENTS_BY_POST_ID_QUERY =
      "SELECT * FROM comments WHERE post_id = ?";
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

  private Comment toComment(Map<String, Object> mp) {
    return Comment.builder()
        .id((Integer) mp.get("id"))
        .user((String) mp.get("user"))
        .content((String) mp.get("content"))
        .postId((Integer) mp.get("post_id"))
        .build();
  }
}

package com.commenter.service;

import com.commenter.model.Comment;
import com.commenter.model.CreateEditCommentRequest;
import com.commenter.model.ResponseVO;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommentService {

  private static final String GET_ALL_COMMENTS_BY_POST_ID_QUERY =
      "SELECT * FROM comments WHERE post_id = ?";
  private static final String GET_COMMENT_BY_ID_AND_USER_QUERY =
      "SELECT * FROM comments WHERE id = ? AND \"user\" = ?";
  private static final String CREATE_NEW_COMMENT_QUERY =
      "INSERT INTO comments (\"user\", content, post_id) VALUES (?, ?, ?) RETURNING id, \"user\", content, post_id";
  private static final String UPDATE_COMMENT_BY_ID_QUERY =
      "UPDATE comments SET content = ? WHERE id = ?";
  private static final String DELETE_COMMENT_BY_ID_QUERY =
      "DELETE FROM comments WHERE id = ?";
  private final DatabaseService databaseService;
  private final PostService postService;

  @Inject
  public CommentService(PostService postService, DatabaseService databaseService) {
    this.postService = postService;
    this.databaseService = databaseService;
  }

  public ResponseVO<List<Comment>> getAllCommentsByPostId(int postId) {
    if (postService.getPostById(postId).getData() == null) {
      return ResponseVO.<List<Comment>>builder()
          .errors(Collections.singletonList("Post data not found"))
          .build();
    }
    try {
      List<Comment> comments =  databaseService.executeQuery(GET_ALL_COMMENTS_BY_POST_ID_QUERY, postId)
          .stream()
          .map(this::toComment)
          .toList();

      return ResponseVO.<List<Comment>>builder().data(comments).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get all comments by postId", e);
    }
  }

  public ResponseVO<Comment> createNewComment(int postId, CreateEditCommentRequest request) {
    List<String> errors = validateRequest(request);

    if (postService.getPostById(postId).getData() == null) {
      errors.add("Post data not found");
    }

    if (!errors.isEmpty()) {
      return ResponseVO.<Comment>builder()
          .errors(errors)
          .build();
    }
    Map<String, Object> result = databaseService.executeQuery(
            CREATE_NEW_COMMENT_QUERY,
            request.getUser(), request.getContent(), postId)
        .stream()
        .findFirst()
        .orElse(null);
    if (result != null) {
      postService.modifyPostCommentById(postId, Boolean.TRUE);
      return toCommentResponseVO(result);
    } else {
      throw new RuntimeException("Failed to create a new comment");
    }
  }

  public ResponseVO<Boolean> editCommentById(int commentId, CreateEditCommentRequest request) {
    List<String> errors = validateRequest(request);

    if (!errors.isEmpty()) {
      return ResponseVO.<Boolean>builder()
          .errors(errors)
          .build();
    }
    try {
      if (databaseService.executeQuery(GET_COMMENT_BY_ID_AND_USER_QUERY, commentId, request.getUser()).isEmpty()) {
        return ResponseVO.<Boolean>builder().errors(Collections.singletonList("Comment data not found")).build();
      }
      databaseService.executeUpdate(UPDATE_COMMENT_BY_ID_QUERY, request.getContent(), commentId);
      return ResponseVO.<Boolean>builder().data(Boolean.TRUE).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to edit comment");
    }
  }

  public ResponseVO<Boolean> deleteCommentById(int commentId, int postId, String user) {
    if (user == null) {
      return ResponseVO.<Boolean>builder().errors(Collections.singletonList("The user field cannot be empty")).build();
    }
    try {
      List<Map<String, Object>> commentResult =
          databaseService.executeQuery(GET_COMMENT_BY_ID_AND_USER_QUERY, commentId, user);
      if (commentResult.isEmpty()) {
        return ResponseVO.<Boolean>builder().errors(Collections.singletonList("Comment data not found")).build();
      }
      if (toComment(commentResult.getFirst()).getPostId() != postId) {
        return ResponseVO.<Boolean>builder().errors(Collections.singletonList("Post id invalid")).build();
      }
      databaseService.executeUpdate(DELETE_COMMENT_BY_ID_QUERY, commentId);
      postService.modifyPostCommentById(postId ,Boolean.FALSE);
      return ResponseVO.<Boolean>builder().data(Boolean.TRUE).build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete comment");
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

  private ResponseVO<Comment> toCommentResponseVO(Map<String, Object> mp) {
    return ResponseVO.<Comment>builder().data(toComment(mp)).build();
  }

  private List<String> validateRequest(CreateEditCommentRequest request) {
    List<String> errors = new ArrayList<>();
    if (request.getUser() == null || request.getUser().isEmpty()) {
      errors.add("The user field cannot be empty");
    }
    if (request.getContent() == null || request.getContent().isEmpty()) {
      errors.add("The content field cannot be empty");
    }
    return errors;
  }
}

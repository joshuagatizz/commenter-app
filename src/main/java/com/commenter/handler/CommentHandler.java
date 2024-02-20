package com.commenter.handler;

import com.commenter.model.Comment;
import com.commenter.model.CreateEditCommentRequest;
import com.commenter.model.DeletePostCommentRequest;
import com.commenter.model.ResponseVO;
import com.commenter.service.CommentService;
import com.commenter.service.helper.ResponseHelper;
import com.google.inject.Inject;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import java.util.List;

import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;

public class CommentHandler implements Handler {

  private final CommentService commentService;

  @Inject
  public CommentHandler(CommentService commentService) {
    this.commentService = commentService;
  }

  @Override
  public void handle(Context context) {
    if (context.getRequest().getMethod().isGet()) {
      handleGet(context);
    } else if (context.getRequest().getMethod().isPost()) {
      handlePost(context);
    } else if (context.getRequest().getMethod().isPut()) {
      handlePut(context);
    } else if (context.getRequest().getMethod().isDelete()) {
      handleDelete(context);
    } else {
      context.render(json(ResponseHelper.status(ResponseHelper.HttpStatus.METHOD_NOT_ALLOWED)));
    }
  }

  private void handleGet(Context context) {
    int postId = Integer.parseInt(context.getAllPathTokens().get("postId"));
    ResponseVO<List<Comment>> result = commentService.getAllCommentsByPostId(postId);
    renderResponse(context, result);
  }

  private void handlePost(Context context) {
    int postId = Integer.parseInt(context.getAllPathTokens().get("postId"));
    context.parse(fromJson(CreateEditCommentRequest.class))
        .then(request -> {
          ResponseVO<Comment> result = commentService.createNewComment(postId, request);
          renderResponse(context, result);
        });
  }

  private void handlePut(Context context) {
    int commentId = Integer.parseInt(context.getAllPathTokens().get("commentId"));
    context.parse(fromJson(CreateEditCommentRequest.class))
        .then(request -> {
          ResponseVO<Boolean> result = commentService.editCommentById(commentId, request);
          renderResponse(context, result);
        });
  }

  private void handleDelete(Context context) {
    int postId = Integer.parseInt(context.getAllPathTokens().get("postId"));
    int commentId = Integer.parseInt(context.getAllPathTokens().get("commentId"));
    context.parse(fromJson(DeletePostCommentRequest.class)).then(request -> {
      ResponseVO<Boolean> result = commentService.deleteCommentById(commentId, postId, request.getUser());
      renderResponse(context, result);
    });
  }

  private <T> void renderResponse(Context context, ResponseVO<T> result) {
    if (result.getErrors() != null) {
      context.render(json(ResponseHelper.badRequest(result.getErrors())));
    } else {
      context.render(json(ResponseHelper.ok(result.getData())));
    }
  }
}
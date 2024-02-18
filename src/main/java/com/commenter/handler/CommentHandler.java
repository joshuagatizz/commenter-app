package com.commenter.handler;

import com.commenter.model.CreateEditCommentRequest;
import com.commenter.service.CommentService;
import com.commenter.service.helper.ResponseHelper;
import com.google.inject.Inject;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import java.util.Collections;

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
      int postId = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.render(json(ResponseHelper.ok(commentService.getAllCommentsByPostId(postId))));
    } else if (context.getRequest().getMethod().isPost()) {
      int postId = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.parse(fromJson(CreateEditCommentRequest.class))
          .then(request -> context.render(
              json(ResponseHelper.ok(commentService.createNewComment(postId, request)))));
    } else if (context.getRequest().getMethod().isPut()) {
      int commentId = Integer.parseInt(context.getAllPathTokens().get("commentId"));
      context.parse(fromJson(CreateEditCommentRequest.class))
          .then(request -> context.render(
              json(ResponseHelper.ok(commentService.editCommentById(commentId, request)))));
    } else {
      context.render(json(ResponseHelper.badRequest(Collections.singletonList("Method not allowed"))));
    }
  }
}

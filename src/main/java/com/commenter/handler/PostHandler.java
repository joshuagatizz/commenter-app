package com.commenter.handler;

import com.commenter.model.CreateEditPostRequest;
import com.commenter.model.DeletePostCommentRequest;
import com.commenter.model.Post;
import com.commenter.model.ResponseVO;
import com.commenter.service.PostService;
import com.commenter.service.helper.ResponseHelper;
import com.google.inject.Inject;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;

public class PostHandler implements Handler {

  private final PostService postService;

  @Inject
  public PostHandler(PostService postService) {
    this.postService = postService;
  }

  @Override
  public void handle(Context context) {
    if (context.getRequest().getMethod().isGet()) {
      context.render(json(ResponseHelper.ok(postService.getAllPosts().getData())));
    } else if (context.getRequest().getMethod().isPost()) {
      context.parse(fromJson(CreateEditPostRequest.class))
          .then(request -> {
            ResponseVO<Post> result = postService.createNewPost(request);
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
              context.render(json(ResponseHelper.badRequest(result.getErrors())));
            } else {
              context.render(json(ResponseHelper.ok(result.getData())));
            }
          });
    } else if (context.getRequest().getMethod().isPut()) {
      int id = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.parse(fromJson(CreateEditPostRequest.class))
          .then(request -> {
            ResponseVO<Boolean> result = postService.editPostById(id, request);
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
              context.render(json(ResponseHelper.badRequest(result.getErrors())));
            } else {
              context.render(json(ResponseHelper.ok(result.getData())));
            }
          });
    } else if (context.getRequest().getMethod().isDelete()) {
      int id = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.parse(fromJson(DeletePostCommentRequest.class))
          .then(request -> {
            ResponseVO<Boolean> result = postService.deletePostById(id, request.getUser());
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
              context.render(json(ResponseHelper.notFound(Boolean.FALSE)));
            } else {
              context.render(json(ResponseHelper.ok(result.getData())));
            }
          });
    } else {
      context.render(json(ResponseHelper.status(ResponseHelper.HttpStatus.METHOD_NOT_ALLOWED)));
    }
  }
}

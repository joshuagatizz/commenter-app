package com.commenter.handler;

import com.commenter.model.CreateEditPostRequest;
import com.commenter.service.PostService;
import com.commenter.service.helper.ResponseHelper;
import com.google.inject.Inject;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import java.util.Collections;

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
      context.render(json(ResponseHelper.ok(postService.getAllPosts())));
    } else if (context.getRequest().getMethod().isPost()) {
      context.parse(fromJson(CreateEditPostRequest.class))
          .then(request -> context.render(json(ResponseHelper.ok(postService.createNewPost(request)))));
    } else if (context.getRequest().getMethod().isPut()) {
      int id = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.parse(fromJson(CreateEditPostRequest.class))
          .then(request -> context.render(
              json(ResponseHelper.ok(postService.editPostById(id, request)))));
    } else if (context.getRequest().getMethod().isDelete()) {
      int id = Integer.parseInt(context.getAllPathTokens().get("postId"));
      context.render(json(ResponseHelper.ok(postService.deletePostById(id))));
    } else {
      context.render(json(ResponseHelper.status(ResponseHelper.HttpStatus.METHOD_NOT_ALLOWED)));
    }
  }
}

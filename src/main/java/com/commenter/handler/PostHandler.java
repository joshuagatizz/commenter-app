package com.commenter.handler;

import com.commenter.model.CreateNewPostRequest;
import com.commenter.service.PostService;
import com.commenter.service.helper.ResponseHelper;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;

public class PostHandler implements Handler {

  private final PostService postService = new PostService();

  @Override
  public void handle(Context context) {
    if (context.getRequest().getMethod().isGet()) {
      context.render(json(ResponseHelper.ok(postService.getAllPosts())));
    } else if (context.getRequest().getMethod().isPost()) {
      context.parse(fromJson(CreateNewPostRequest.class))
          .then(request -> context.render(json(ResponseHelper.ok(postService.createNewPost(request)))));
    }
  }
}

package com.commenter.handler;

import com.commenter.service.PostService;
import com.commenter.service.helper.ResponseHelper;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import static ratpack.core.jackson.Jackson.json;

public class PostHandler implements Handler {

  private final PostService postService = new PostService();

  @Override
  public void handle(Context context) {
    context.render(json(ResponseHelper.ok(postService.getAllPosts())));
  }
}

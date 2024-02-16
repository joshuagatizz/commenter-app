package com.commenter.handler;

import com.commenter.model.Post;
import com.commenter.service.PostService;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

import static ratpack.core.jackson.Jackson.json;

public class PostHandler implements Handler {

  private final PostService postService = new PostService();
  @Override
  public void handle(Context context) throws Exception {
    context.render(json(Post.builder()
        .id(1)
        .title("test")
        .content("content")
        .user("joshu")
        .build()));
  }
}

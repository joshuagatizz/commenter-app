package com.commenter.handler;

import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

public class PostHandler implements Handler {
  @Override
  public void handle(Context context) throws Exception {
    context.render("get all posts");
  }
}

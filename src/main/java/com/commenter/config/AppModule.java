package com.commenter.config;

import com.commenter.handler.CommentHandler;
import com.commenter.handler.PostHandler;
import com.commenter.service.CommentService;
import com.commenter.service.DatabaseService;
import com.commenter.service.PostService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AppModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(PostHandler.class).in(Singleton.class);
    bind(PostService.class).in(Singleton.class);

    bind(CommentHandler.class).in(Singleton.class);
    bind(CommentService.class).in(Singleton.class);

    bind(DatabaseService.class).in(Singleton.class);
  }
}

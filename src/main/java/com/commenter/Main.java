package com.commenter;

import com.commenter.config.AppModule;
import com.commenter.handler.CommentHandler;
import com.commenter.handler.CustomClientErrorHandler;
import com.commenter.handler.CustomServerErrorHandler;
import com.commenter.handler.PostHandler;
import ratpack.core.error.ClientErrorHandler;
import ratpack.core.error.ServerErrorHandler;
import ratpack.core.server.RatpackServer;
import ratpack.guice.Guice;

public class Main {
  public static void main(String[] args) throws Exception {
    RatpackServer.start(server -> server
        .serverConfig(config -> config.port(8080))
        .registry(Guice.registry(bindings -> {
          bindings.module(AppModule.class);
          bindings.bind(ClientErrorHandler.class, CustomClientErrorHandler.class);
          bindings.bind(ServerErrorHandler.class, CustomServerErrorHandler.class);
        }))
        .handlers(chain -> chain
            .prefix("api", api -> api
                .prefix("posts", postApi -> postApi
                    .path(path -> path.byMethod(method -> method
                        .get(PostHandler.class)
                        .post(PostHandler.class)))
                    .prefix(":postId", postId -> postId
                        .path(path -> path.byMethod(method -> method
                            .put(PostHandler.class)
                            .delete(PostHandler.class)
                        ))
                        .prefix("comments", comment -> comment
                            .path(path -> path.byMethod(method -> method
                                .get(CommentHandler.class)
                                .post(CommentHandler.class)
                            ))
                            .prefix(":commentId", commentId -> commentId
                                .path(path -> path.byMethod(method -> method
                                    .put(CommentHandler.class)
                                    .delete(CommentHandler.class)
                                ))
                            )
                        )
                    )
                )
            )
        )
    );
  }
}

package com.commenter;

import com.commenter.config.AppModule;
import com.commenter.handler.PostHandler;
import ratpack.core.server.RatpackServer;
import ratpack.guice.Guice;

public class Main {
  public static void main(String[] args) throws Exception {
    RatpackServer.start(server -> server
        .serverConfig(config -> config.port(8080))
        .registry(Guice.registry(b -> b.module(AppModule.class)))
        .handlers(chain -> chain
            .prefix("api", api -> api
                .prefix("posts", postApi -> postApi
                    .path(path -> path.byMethod(method -> method
                        .get(PostHandler.class)
                        .post(PostHandler.class)))
                    .prefix(":id", id -> id
                        .path(path -> path.byMethod(method -> method
                            .put(PostHandler.class)
                            .delete(PostHandler.class)
                        ))
                    )
                )
            )
        )
    );
  }
}

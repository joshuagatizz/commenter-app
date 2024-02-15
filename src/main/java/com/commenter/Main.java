package com.commenter;

import com.commenter.handler.PostHandler;
import ratpack.core.server.RatpackServer;

public class Main {
  public static void main(String[] args) throws Exception {
    RatpackServer.start(server -> server
        .serverConfig(config-> {
          config.port(8080);
        })
        .handlers(chain -> chain
            .get(ctx -> ctx.render("Hello World!"))
            .get(":name", ctx -> ctx.render("Hello " + ctx.getPathTokens().get("name") + "!"))
            .prefix("api", apiPath -> apiPath
                .prefix("post", postPath -> postPath
                    .get("", new PostHandler())
                )
            )
        )
    );
  }
}
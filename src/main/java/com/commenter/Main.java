package com.commenter;

import com.commenter.handler.PostHandler;
import ratpack.core.server.RatpackServer;

public class Main {
  public static void main(String[] args) throws Exception {
    RatpackServer.start(server -> server
        .serverConfig(config -> config.port(8080))
        .handlers(chain -> chain
            .prefix("api", api -> api
                .prefix("post", postPath -> postPath
                    .get("", new PostHandler())
                )
            )
        )
    );
  }
}
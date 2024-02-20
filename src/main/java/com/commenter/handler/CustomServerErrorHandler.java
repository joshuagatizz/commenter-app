package com.commenter.handler;

import com.commenter.service.helper.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.error.ServerErrorHandler;
import ratpack.core.handling.Context;

import static ratpack.core.jackson.Jackson.json;

public class CustomServerErrorHandler implements ServerErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomServerErrorHandler.class);

  @Override
  public void error(Context context, Throwable throwable) throws Exception {
    LOGGER.error("Internal Server Error", throwable);
    context.render(json(ResponseHelper.internalServerError()));
  }
}

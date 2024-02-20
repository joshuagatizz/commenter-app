package com.commenter.handler;

import com.commenter.service.helper.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.error.ClientErrorHandler;
import ratpack.core.handling.Context;

import static ratpack.core.jackson.Jackson.json;

public class CustomClientErrorHandler implements ClientErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientErrorHandler.class);
  @Override
  public void error(Context context, int statusCode) throws Exception {
    ResponseHelper.HttpStatus status = switch (statusCode) {
      case 400 -> ResponseHelper.HttpStatus.BAD_REQUEST;
      case 404 -> ResponseHelper.HttpStatus.NOT_FOUND;
      case 405 -> ResponseHelper.HttpStatus.METHOD_NOT_ALLOWED;
      default -> ResponseHelper.HttpStatus.INTERNAL_SERVER_ERROR;
    };
    LOGGER.error(status.getMessage());
    context.render(json(ResponseHelper.status(status)));
  }
}
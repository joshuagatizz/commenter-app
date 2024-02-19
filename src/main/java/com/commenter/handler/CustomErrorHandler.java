package com.commenter.handler;

import com.commenter.service.helper.ResponseHelper;
import ratpack.core.error.ClientErrorHandler;
import ratpack.core.handling.Context;

import static ratpack.core.jackson.Jackson.json;

public class CustomErrorHandler implements ClientErrorHandler {
  @Override
  public void error(Context context, int statusCode) throws Exception {
    ResponseHelper.HttpStatus status = switch (statusCode) {
      case 400 -> ResponseHelper.HttpStatus.BAD_REQUEST;
      case 404 -> ResponseHelper.HttpStatus.NOT_FOUND;
      case 405 -> ResponseHelper.HttpStatus.METHOD_NOT_ALLOWED;
      default -> ResponseHelper.HttpStatus.INTERNAL_SERVER_ERROR;
    };
    context.render(json(ResponseHelper.status(status)));
  }
}
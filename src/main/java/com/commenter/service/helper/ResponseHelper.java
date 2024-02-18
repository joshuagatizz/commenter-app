package com.commenter.service.helper;

import com.commenter.model.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ResponseHelper {
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private int code;
    private String message;
  }
  public static <T> Response<T> ok(T data) {
    return status(HttpStatus.OK, data);
  }

  public static <T> Response<T> badRequest(List<String> errors) {
    return status(HttpStatus.BAD_REQUEST, null, errors);
  }

  public static <T> Response<T> notFound(T data) {
    return status(HttpStatus.NOT_FOUND, data);
  }

  public static <T> Response<T> internalServerError() {
    return status(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static <T> Response<T> status(HttpStatus status) {
    return status(status, null);
  }

  public static <T> Response<T> status(HttpStatus status, T data) {
    return status(status, data, null);
  }

  public static <T> Response<T> status(HttpStatus status, T data, List<String> errors) {
    return Response.<T>builder().code(status.getCode()).status(status.getMessage()).data(data).errors(errors).build();
  }
}

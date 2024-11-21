package com.example.clientapp.util;


public class Triple<String, Boolean, T> {

  public String getMessage() {
    return message;
  }

  public Boolean getStatus() {
    return status;
  }

  public T getData() {
    return data;
  }

  private final String message;

  @Override
  public java.lang.String toString() {
    return "Triple{" +
        "message=" + message +
        ", status=" + status +
        ", data=" + data +
        '}';
  }

  private final Boolean status;
  private final T data;

  public Triple(String message, Boolean status, T data) {
    this.message = message;
    this.status = status;
    this.data = data;
  }


}
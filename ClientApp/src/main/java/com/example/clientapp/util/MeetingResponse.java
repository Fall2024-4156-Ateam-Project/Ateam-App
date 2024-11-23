package com.example.clientapp.util;

/**
 * 用于表示会议相关操作的响应。
 */
public class MeetingResponse {
  private String message;
  private boolean status;

  public MeetingResponse() {}

  public MeetingResponse(String message, boolean status) {
    this.message = message;
    this.status = status;
  }

  // Getters 和 Setters
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }
}

package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

  private int requesterId;
  private int tid;

  @JsonProperty("status")
  private CommonTypes.RequestStatus status;
  private String description;
  private String doctor;

  public Request() {
  }

  public void setStatus(CommonTypes.RequestStatus status) {
    this.status = status;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public int getRequester() {
    return requesterId;
  }
  
  public int getTimeSlot() {
    return tid;
  }
  
  public void setRequester(int requesterId) {
    this.requesterId = requesterId;
  }
  
  public void setTimeSlot(int tid) {
    this.tid = tid;
  }
  
  public String getDescription() {
    return description;
  }
  
  public CommonTypes.RequestStatus getStatus() {
    return status;
  }
  public String getDocotr() {
    return doctor;
  }

  public void setDoctor(String doctor) {
    this.doctor = doctor;
  }
  
}

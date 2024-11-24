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
  private String requesterName;
  private String requesterEmail;

  public Request() {
  }

  public void setStatus(CommonTypes.RequestStatus status) {
    this.status = status;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public int getRequesterId() {
    return requesterId;
  }
  
  public int getTid() {
    return tid;
  }
  
  public void setRequesterId(int requesterId) {
    this.requesterId = requesterId;
  }
  
  public void setTid(int tid) {
    this.tid = tid;
  }
  
  public String getDescription() {
    return description;
  }
  
  public CommonTypes.RequestStatus getStatus() {
    return status;
  }
  public String getRequesterName() {
    return requesterName;
  }
  public void setRequesterName(String requesterName) {
    this.requesterName = requesterName;
  }
  public void setRequesterEmail(String requesterEmail) {
    this.requesterEmail = requesterEmail;
  }
  public String getRequesterEmail() {
    return requesterEmail;
  }
  
}

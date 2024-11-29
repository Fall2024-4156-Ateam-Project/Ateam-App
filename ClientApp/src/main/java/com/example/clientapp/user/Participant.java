package com.example.clientapp.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Ignore unknown properties to prevent errors during deserialization
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {

  private int pid;

  @JsonProperty("meeting")
  private Meeting meeting; // Reference to the associated Meeting

  @JsonProperty("user")
  private User user; // Reference to the User

  private String role;
  private String joinAt;
  private String status;

  // Constructors
  public Participant() {
    // Default constructor
  }

  public Participant(int pid, Meeting meeting, User user, String role, String joinAt, String status) {
    this.pid = pid;
    this.meeting = meeting;
    this.user = user;
    this.role = role;
    this.joinAt = joinAt;
    this.status = status;
  }

  // Getters and Setters
  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public Meeting getMeeting() {
    return meeting;
  }

  public void setMeeting(Meeting meeting) {
    this.meeting = meeting;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getJoinAt() {
    return joinAt;
  }

  public void setJoinAt(String joinAt) {
    this.joinAt = joinAt;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

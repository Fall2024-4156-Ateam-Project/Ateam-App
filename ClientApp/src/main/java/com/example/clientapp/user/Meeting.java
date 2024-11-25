package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.CommonTypes.Availability;
import com.example.clientapp.util.CommonTypes.Day;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

public class Meeting {
  private String description;
  private CommonTypes.Recurrence recurrence;
  private CommonTypes.MeetingType type;
  private CommonTypes.MeetingStatus status;
  private String organizerEmail;
  private String participantEmail;
  private LocalTime  startTime;
  private LocalTime  endTime;
  @JsonProperty("startDay")
  private CommonTypes.Day startDay;
  @JsonProperty("endDay")
  private CommonTypes.Day endDay;
  private LocalTime CreatedAt;

  // Constructors
  public Meeting() {
    // Default constructor
  }

  public Meeting(String description, CommonTypes.Recurrence recurrence,
                 CommonTypes.MeetingType type, CommonTypes.MeetingStatus status,
                 String organizerEmail, String participantEmail,
                 LocalTime startTime, LocalTime endTime, CommonTypes.Day startDay, CommonTypes.Day endDay) {
    this.description = description;
    this.recurrence = recurrence;
    this.type = type;
    this.status = status;
    this.organizerEmail = organizerEmail;
    this.participantEmail = participantEmail;
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDay=startDay;
    this.endDay=endDay;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOrganizerEmail() {
    return organizerEmail;
  }

  public void setOrganizerEmail(String organizerEmail) {
    this.organizerEmail = organizerEmail;
  }

  public String getParticipantEmail() {
    return participantEmail;
  }

  public void setParticipantEmail(String participantEmail) {
    this.participantEmail = participantEmail;
  }

  public CommonTypes.Recurrence getRecurrence() {
    return recurrence;
  }

  public void setRecurrence(CommonTypes.Recurrence recurrence) {
    this.recurrence = recurrence;
  }

  public CommonTypes.MeetingType getType() {
    return type;
  }

  public void setType(CommonTypes.MeetingType type) {
    this.type = type;
  }

  public CommonTypes.MeetingStatus getStatus() {
    return status;
  }

  public void setStatus(CommonTypes.MeetingStatus status) {
    this.status = status;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime  startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }


  public LocalTime getCreatedAt() {
    return CreatedAt;
  }

  public void setCreatedAt(LocalTime createdAt) {
    CreatedAt = createdAt;
  }

  public CommonTypes.Day getStartDay() {
    return startDay;
  }

  public void setStartDay(CommonTypes.Day startDay) {
    this.startDay = startDay;
  }

  public CommonTypes.Day getEndDay() {
    return endDay;
  }

  public void setEndDay(CommonTypes.Day endDay) {
    this.endDay = endDay;
  }
}

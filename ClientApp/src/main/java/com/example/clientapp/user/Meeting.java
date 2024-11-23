package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class Meeting {
  private String description;
  private CommonTypes.Recurrence recurrence;
  private CommonTypes.MeetingType type;
  private CommonTypes.MeetingStatus status;
  private String organizerEmail;
  private String participantEmail;
  private LocalDateTime  startTime;
  private LocalDateTime  endTime;
  private LocalDateTime CreatedAt;

  // Constructors
  public Meeting() {
    // Default constructor
  }

  public Meeting(String description, CommonTypes.Recurrence recurrence,
                 CommonTypes.MeetingType type, CommonTypes.MeetingStatus status,
                 String organizerEmail, String participantEmail,
                 LocalDateTime startTime, LocalDateTime endTime) {
    this.description = description;
    this.recurrence = recurrence;
    this.type = type;
    this.status = status;
    this.organizerEmail = organizerEmail;
    this.participantEmail = participantEmail;
    this.startTime = startTime;
    this.endTime = endTime;
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

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime  startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }


  public LocalDateTime getCreatedAt() {
    return CreatedAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    CreatedAt = createdAt;
  }
}

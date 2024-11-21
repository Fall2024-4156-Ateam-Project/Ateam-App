package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.CommonTypes.Availability;
import com.example.clientapp.util.CommonTypes.Day;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSlot {

  private LocalTime startTime;
  private LocalTime endTime;

  @JsonProperty("day")
  private CommonTypes.Day day;
  @JsonProperty("availability")
  private CommonTypes.Availability availability;

  public TimeSlot() {
  }

  public Day getDay() {
    return day;
  }

  public void setDay(Day day) {
    this.day = day;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public Availability getAvailability() {
    return availability;
  }

  public void setAvailability(Availability availability) {
    this.availability = availability;
  }
}

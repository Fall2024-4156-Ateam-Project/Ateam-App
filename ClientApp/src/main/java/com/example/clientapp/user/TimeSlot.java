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
  private int tid;

  @JsonProperty("day")
  private CommonTypes.Day day;

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public Day getStartDay() {
    return startDay;
  }

  public void setStartDay(Day startDay) {
    this.startDay = startDay;
  }

  public Day getEndDay() {
    return endDay;
  }

  @Override
  public String toString() {
    return "TimeSlot{" +
        "startTime=" + startTime +
        ", endTime=" + endTime +
        ", day=" + day +
        ", startDay=" + startDay +
        ", endDay=" + endDay +
        ", availability=" + availability +
        '}';
  }

  public void setEndDay(Day endDay) {
    this.endDay = endDay;
  }

  @JsonProperty("startDay")
  private CommonTypes.Day startDay;
  @JsonProperty("endDay")
  private CommonTypes.Day endDay;
  @JsonProperty("availability")
  private CommonTypes.Availability availability;

  public TimeSlot(LocalTime startTime, LocalTime endTime, Day startDay, Day endDay,
      Availability availability, int tid) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDay = startDay;
    this.endDay = endDay;
    this.availability = availability;
    this.tid = tid;
  }

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

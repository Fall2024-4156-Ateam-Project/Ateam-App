package com.example.clientapp.util;

/**
 * This class contains all the enumerations used for other classes. These enumerations represent and
 * list the different meeting types, recurrence options, meeting statuses, roles, participant
 * statuses, daysOfWeek, Availability and request statuses.
 */
public class CommonTypes {

  /**
   * Represents the roles of users.
   */
  public enum Role {
    doctor,
    patient
  }

  public enum Gender {
    male,
    female
  }

  /**
   * Represents the types of meetings.
   */
  public enum MeetingType {
    group,
    one_on_one
  }

  /**
   * Represents the recurrence options.
   */
  public enum Recurrence {
    daily,
    weekly,
    monthly,
    none
  }

  /**
   * Represents the meeting statuses.
   */
  public enum MeetingStatus {
    Valid,
    Invalid
  }

  /**
   * Represents the role types.
   */
  public enum GenericRole {
    participant,
    organizer
  }

  /**
   * Represents the status of participants.
   */
  public enum ParticipantStatus {
    decline,
    accept,
    waiting
  }

  /**
   * Represents the days of the Week.
   */
  public enum Day {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
  }

  /**
   * Represents the availability of timeslots.
   */
  public enum Availability {
    available, busy
  }

  /**
   * Represents the request statuses.
   */
  public enum RequestStatus {
    undecided, approved, rejected
  }

}

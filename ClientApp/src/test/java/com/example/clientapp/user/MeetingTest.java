package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeetingTest {

  @Test
  void testConstructorAndBasicFields() {
    // Arrange
    int mid = 1;
    String description = "Project discussion";
    CommonTypes.Recurrence recurrence = CommonTypes.Recurrence.weekly;
    CommonTypes.MeetingType type = CommonTypes.MeetingType.group;
    CommonTypes.MeetingStatus status = CommonTypes.MeetingStatus.Valid;
    String organizerEmail = "organizer@example.com";
    String participantEmail = "participant@example.com";
    LocalTime startTime = LocalTime.of(9, 0);
    LocalTime endTime = LocalTime.of(10, 0);
    CommonTypes.Day startDay = CommonTypes.Day.Monday;
    CommonTypes.Day endDay = CommonTypes.Day.Monday;

    // Act
    Meeting meeting = new Meeting(mid, description, recurrence, type, status,
            organizerEmail, participantEmail, startTime, endTime, startDay, endDay);

    // Assert
    assertEquals(mid, meeting.getMid());
    assertEquals(description, meeting.getDescription());
    assertEquals(recurrence, meeting.getRecurrence());
    assertEquals(type, meeting.getType());
    assertEquals(status, meeting.getStatus());
    assertEquals(organizerEmail, meeting.getOrganizerEmail());
    assertEquals(participantEmail, meeting.getParticipantEmail());
    assertEquals(startTime, meeting.getStartTime());
    assertEquals(endTime, meeting.getEndTime());
    assertEquals(startDay, meeting.getStartDay());
    assertEquals(endDay, meeting.getEndDay());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Meeting meeting = new Meeting();

    // Set values
    meeting.setMid(2);
    meeting.setDescription("Team meeting");
    meeting.setRecurrence(CommonTypes.Recurrence.daily);
    meeting.setType(CommonTypes.MeetingType.one_on_one);
    meeting.setStatus(CommonTypes.MeetingStatus.Valid);
    meeting.setOrganizerEmail("team_lead@example.com");
    meeting.setParticipantEmail("member@example.com");
    meeting.setStartTime(LocalTime.of(14, 0));
    meeting.setEndTime(LocalTime.of(15, 0));
    meeting.setStartDay(CommonTypes.Day.Tuesday);
    meeting.setEndDay(CommonTypes.Day.Tuesday);

    // Act & Assert
    assertEquals(2, meeting.getMid());
    assertEquals("Team meeting", meeting.getDescription());
    assertEquals(CommonTypes.Recurrence.daily, meeting.getRecurrence());
    assertEquals(CommonTypes.MeetingType.one_on_one, meeting.getType());
    assertEquals(CommonTypes.MeetingStatus.Valid, meeting.getStatus());
    assertEquals("team_lead@example.com", meeting.getOrganizerEmail());
    assertEquals("member@example.com", meeting.getParticipantEmail());
    assertEquals(LocalTime.of(14, 0), meeting.getStartTime());
    assertEquals(LocalTime.of(15, 0), meeting.getEndTime());
    assertEquals(CommonTypes.Day.Tuesday, meeting.getStartDay());
    assertEquals(CommonTypes.Day.Tuesday, meeting.getEndDay());
  }

  @Test
  void testDefaultConstructor() {
    // Arrange & Act
    Meeting meeting = new Meeting();

    // Assert
    assertEquals(0, meeting.getMid());
    assertNull(meeting.getDescription());
    assertNull(meeting.getRecurrence());
    assertNull(meeting.getType());
    assertNull(meeting.getStatus());
    assertNull(meeting.getOrganizerEmail());
    assertNull(meeting.getParticipantEmail());
    assertNull(meeting.getStartTime());
    assertNull(meeting.getEndTime());
    assertNull(meeting.getStartDay());
    assertNull(meeting.getEndDay());
    assertNull(meeting.getParticipants());
  }
}

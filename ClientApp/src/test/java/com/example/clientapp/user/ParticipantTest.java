package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

  @Test
  void testConstructorAndBasicFields() {
    // Arrange
    int pid = 1;
    Meeting meeting = new Meeting();
    User user = new User("user@example.com", "password", "John Doe", CommonTypes.Role.patient);
    String role = "Organizer";
    String joinAt = "2024-12-01T10:00:00";
    String status = "Active";

    // Act
    Participant participant = new Participant(pid, meeting, user, role, joinAt, status);

    // Assert
    assertEquals(pid, participant.getPid());
    assertEquals(meeting, participant.getMeeting());
    assertEquals(user, participant.getUser());
    assertEquals(role, participant.getRole());
    assertEquals(joinAt, participant.getJoinAt());
    assertEquals(status, participant.getStatus());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Participant participant = new Participant();
    Meeting meeting = new Meeting();
    User user = new User("user@example.com", "password", "John Doe", CommonTypes.Role.patient);

    // Act
    participant.setPid(2);
    participant.setMeeting(meeting);
    participant.setUser(user);
    participant.setRole("Participant");
    participant.setJoinAt("2024-12-02T14:30:00");
    participant.setStatus("Inactive");

    // Assert
    assertEquals(2, participant.getPid());
    assertEquals(meeting, participant.getMeeting());
    assertEquals(user, participant.getUser());
    assertEquals("Participant", participant.getRole());
    assertEquals("2024-12-02T14:30:00", participant.getJoinAt());
    assertEquals("Inactive", participant.getStatus());
  }

  @Test
  void testDefaultConstructor() {
    // Arrange & Act
    Participant participant = new Participant();

    // Assert
    assertEquals(0, participant.getPid());
    assertNull(participant.getMeeting());
    assertNull(participant.getUser());
    assertNull(participant.getRole());
    assertNull(participant.getJoinAt());
    assertNull(participant.getStatus());
  }
}

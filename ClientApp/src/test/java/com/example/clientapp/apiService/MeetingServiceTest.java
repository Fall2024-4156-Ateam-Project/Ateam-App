package com.example.clientapp.apiService;

import com.example.clientapp.user.Meeting;
import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.MeetingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeetingServiceTest {

  @Autowired
  private MeetingService meetingService;

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private ObjectMapper objectMapper;

  private final String FIND_USER_BY_EMAIL_URL = "http://localhost:8080/api/v1/users/findByEmail";
  private final String SAVE_MEETING_URL = "http://localhost:8080/api/v1/meetings/saveMeeting";
  private final String DELETE_MEETING_URL = "http://localhost:8080/api/v1/meetings/deleteMeeting";
  private final String FIND_PARTICIPANTS_BY_USER_URL = "http://localhost:8080/api/v1/participants/findByUser";

  @BeforeEach
  void setup() throws Exception {
    // Mock findUserByEmail response
    Map<String, Object> mockUser = new HashMap<>();
    mockUser.put("uid", 1);
    List<Map<String, Object>> mockUserList = Collections.singletonList(mockUser);

    Mockito.when(restTemplate.exchange(
                    ArgumentMatchers.contains(FIND_USER_BY_EMAIL_URL),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(),
                    Mockito.eq(List.class)))
            .thenReturn(new ResponseEntity<>(mockUserList, HttpStatus.OK));

    // Mock createMeeting response
    Mockito.when(restTemplate.postForEntity(
                    Mockito.eq(SAVE_MEETING_URL),
                    Mockito.any(),
                    Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>("Meeting created successfully.", HttpStatus.CREATED));

    // Mock getMyMeetings response
    String mockMeetingResponse = """
                [
                    {
                        "meeting": {
                            "mid": 101,
                            "type": "ONLINE",
                            "description": "Test Meeting",
                            "status": "SCHEDULED",
                            "startDay": "2024-12-10",
                            "endDay": "2024-12-10",
                            "startTime": "10:00",
                            "endTime": "11:00"
                        },
                        "role": "ORGANIZER"
                    }
                ]
                """;

    Mockito.when(restTemplate.exchange(
                    ArgumentMatchers.contains(FIND_PARTICIPANTS_BY_USER_URL),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(),
                    Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockMeetingResponse, HttpStatus.OK));

    // Mock deleteMeeting response
    Mockito.when(restTemplate.exchange(
                    ArgumentMatchers.contains(DELETE_MEETING_URL),
                    Mockito.eq(HttpMethod.DELETE),
                    Mockito.any(),
                    Mockito.eq(Void.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }

  @Test
  void testCreateMeeting() {
    Meeting meeting = new Meeting();
    meeting.setOrganizerEmail("organizer@example.com");
    meeting.setParticipantEmail("participant@example.com");
    meeting.setDescription("Test Meeting");
    meeting.setType(CommonTypes.MeetingType.one_on_one);
    meeting.setRecurrence(CommonTypes.Recurrence.daily);
    meeting.setStatus(CommonTypes.MeetingStatus.Valid);
    meeting.setStartDay(CommonTypes.Day.Monday);
    meeting.setEndDay(CommonTypes.Day.Monday);
    meeting.setStartTime(LocalTime.of(10, 0));
    meeting.setEndTime(LocalTime.of(11, 0));

    MeetingResponse response = meetingService.createMeeting(meeting).join();
    assertNotNull(response);
    assertEquals("Meeting created successfully.", response.getMessage());
  }

//  @Test
//  void testGetMyMeetings() {
//    String email = "user@example.com";
//
//    List<Map<String, Object>> meetings = meetingService.getMyMeetings(email).join();
//    assertNotNull(meetings);
//    assertEquals(1, meetings.size());
//    assertEquals("Test Meeting", ((Map<?, ?>) meetings.get(0).get("meeting")).get("description"));
//  }
//
//  @Test
//  void testDeleteMeeting() {
//    int meetingId = 101;
//
//    MeetingResponse response = meetingService.deleteMeeting(meetingId);
//    assertNotNull(response);
//    assertEquals("Meeting deleted successfully.", response.getMessage());
//  }
}

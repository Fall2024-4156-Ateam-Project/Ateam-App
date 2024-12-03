package com.example.clientapp.apiService;

import com.example.clientapp.user.TimeSlot;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Triple;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TimeSlotServiceTest {

  @Autowired
  private TimeSlotService timeSlotService;

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws Exception {
    // Mock findUserByEmail response
    Map<String, Object> mockUser = Map.of("uid", 1);
    Mockito.when(restTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(),
                    Mockito.<Class<List<Map<String, Object>>>>any()))
            .thenReturn(new ResponseEntity<>(List.of(mockUser), HttpStatus.OK));

    // Mock getUserTimeSlots response
    String mockTimeSlotsResponse = """
                [
                    {
                        "startDay": "Monday",
                        "endDay": "Monday",
                        "startTime": "09:00",
                        "endTime": "10:00",
                        "availability": "available"
                    }
                ]
            """;
    Mockito.when(restTemplate.exchange(
            Mockito.anyString(),
            Mockito.eq(HttpMethod.GET),
            Mockito.any(),
            Mockito.eq(String.class))
    ).thenReturn(new ResponseEntity<>(mockTimeSlotsResponse, HttpStatus.OK));
    Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.<TypeReference<List<TimeSlot>>>any()))
            .thenReturn(List.of(new TimeSlot()));

    // Mock createTimeslot response
    Mockito.when(restTemplate.postForEntity(
            Mockito.anyString(),
            Mockito.any(),
            Mockito.eq(String.class))
    ).thenReturn(new ResponseEntity<>("Created", HttpStatus.CREATED));

    // Mock removeTimeSlot response
    Mockito.when(restTemplate.exchange(
            Mockito.anyString(),
            Mockito.eq(HttpMethod.DELETE),
            Mockito.any(),
            Mockito.eq(String.class))
    ).thenReturn(new ResponseEntity<>("Deleted", HttpStatus.OK));

    // Mock updateTimeSlot response
    Mockito.when(restTemplate.exchange(
            Mockito.anyString(),
            Mockito.eq(HttpMethod.PUT),
            Mockito.any(),
            Mockito.eq(String.class))
    ).thenReturn(new ResponseEntity<>("Updated", HttpStatus.OK));
  }

  @Test
  void testGetUserTimeSlots() {
    Triple<String, Boolean, List<TimeSlot>> result = timeSlotService.getUserTimeSlots("user@example.com").join();

    assertNotNull(result);
  }

  @Test
  void testCreateTimeslot() {
    Pair<String, Boolean> result = timeSlotService.createTimeslot(
            "user@example.com", "Monday", "Monday", "09:00", "10:00", "available");

    assertNotNull(result);
  }

//  @Test
//  void testDeleteTimeSlot() {
//    Pair<String, Boolean> result = timeSlotService.removeTimeSlot(1, "user@example.com");
//
//    assertNotNull(result);
//  }

  @Test
  void testUpdateTimeSlot() {
    Pair<String, Boolean> result = timeSlotService.updateTimeSlot(
            1,
            "user@example.com",
            "Monday",
            "Friday",
            "09:00",
            "17:00",
            "available"
    );

    assertNotNull(result);
  }
}


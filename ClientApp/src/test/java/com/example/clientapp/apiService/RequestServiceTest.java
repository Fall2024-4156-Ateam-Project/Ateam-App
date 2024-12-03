package com.example.clientapp.apiService;

import com.example.clientapp.user.Request;
import com.example.clientapp.util.CommonTypes.RequestStatus;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Triple;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestServiceTest {

  @Autowired
  private RequestService requestService;

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws Exception {
    // Mocked JSON structure
    String json = """
            [
                {
                    "status": "undecided",
                    "description": "Test Description",
                    "user": {
                        "uid": 1,
                        "name": "John",
                        "email": "john@example.com"
                    },
                    "timeSlot": {
                        "tid": 123
                    }
                }
            ]
        """;

    JsonNode mockNode = new ObjectMapper().readTree(json);
    Mockito.when(objectMapper.readTree(Mockito.anyString())).thenReturn(mockNode);

    // Mock RestTemplate responses
    Mockito.when(restTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(org.springframework.http.HttpMethod.GET),
                    Mockito.any(),
                    Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

    Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

    Mockito.when(restTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(org.springframework.http.HttpMethod.PUT),
                    Mockito.any(),
                    Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>("Update Success", HttpStatus.OK));

    Mockito.when(restTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(org.springframework.http.HttpMethod.DELETE),
                    Mockito.any(),
                    Mockito.eq(String.class)))
            .thenReturn(new ResponseEntity<>("Request Removed", HttpStatus.OK));

    // Mock findUserByEmail
    List<Map<String, Object>> mockUserResponse = new ArrayList<>();
    Map<String, Object> user = new HashMap<>();
    user.put("uid", 1);
    mockUserResponse.add(user);

    Mockito.when(restTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(org.springframework.http.HttpMethod.GET),
                    Mockito.any(),
                    Mockito.eq(List.class)))
            .thenReturn(new ResponseEntity<>(mockUserResponse, HttpStatus.OK));
  }

  @Test
  void testGetUserRequests() {
    String email = "test@example.com";
    try {
      Triple<String, Boolean, List<Request>> result = requestService.getUserRequests(email).get();
      assertNotNull(result);
    } catch (InterruptedException | ExecutionException e) {
      fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  void testGetTimeslotRequests() {
    String tid = "123";
    try {
      Triple<String, Boolean, List<Request>> result = requestService.getTimeslotRequests(tid).get();
      assertNotNull(result);
    } catch (InterruptedException | ExecutionException e) {
      fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  void testCreateRequest() {
    String email = "test@example.com";
    String tid = "123";
    String description = "Test Description";
    String status = RequestStatus.undecided.name();

    Pair<String, Boolean> result = requestService.createRequest(email, tid, description, status);
    assertNotNull(result);
  }

  @Test
  void testUpdateRequestStatus() {
    int requesterId = 1;
    int tid = 123;
    String status = RequestStatus.approved.name();

    Pair<String, Boolean> result = requestService.updateRequestStatus(requesterId, tid, status);
    assertNotNull(result);
  }

  @Test
  void testUpdateRequestDescription() {
    int requesterId = 1;
    int tid = 123;
    String description = "Updated Description";

    Pair<String, Boolean> result = requestService.updateRequestDescription(requesterId, tid, description);
    assertNotNull(result);
  }

  @Test
  void testRemoveRequest() {
    int requesterId = 1;
    int tid = 123;

    Pair<String, Boolean> result = requestService.removeRequest(requesterId, tid);
    assertNotNull(result);
  }

  @Test
  void testFindUserByEmail() {
    String email = "test@example.com";
    Map<String, Object> user = requestService.findUserByEmail(email);
    assertNotNull(user);
    assertEquals(1, user.get("uid"));
  }
}



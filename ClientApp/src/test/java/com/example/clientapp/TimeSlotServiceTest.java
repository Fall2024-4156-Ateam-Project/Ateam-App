package com.example.clientapp.apiService;

import com.example.clientapp.util.CommonTypes.Availability;
import com.example.clientapp.util.CommonTypes.Role;
import com.example.clientapp.user.TimeSlot;
import com.example.clientapp.apiService.apiConfig;
import com.example.clientapp.util.Triple;
import com.example.clientapp.util.Pair;
import com.example.clientapp.user.User;
import com.example.clientapp.util.CommonTypes.Day;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.util.concurrent.ExecutionException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

class TimeSlotServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private TimeSlotService timeSlotService;

  @InjectMocks
  private TimeSlotServiceTest timeSlotServiceTest;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @Test
  void testGetUserTimeSlots_Success() throws Exception {
    // Arrange
    String email = "user@example.com";
    String url = apiConfig.baseApi + apiConfig.TIME_SLOT_GET_ALL_BY_EMAIL + "?email=" + email;

    HttpHeaders headers = new HttpHeaders();
    headers.add("apiKey", "testtestkey");

    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("email", email);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

    String responseJson = "[{\"startTime\":\"09:00\",\"endTime\":\"10:00\",\"startDay\":\"Monday\",\"endDay\":\"Monday\",\"availability\":\"available\",\"tid\":1}]";
    List<TimeSlot> expectedTimeSlots = Arrays.asList(
            new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), Day.Monday, Day.Monday, Availability.available, 1)
    );

    ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    when(restTemplate.exchange(eq(url), eq(org.springframework.http.HttpMethod.GET), eq(request), eq(String.class)))
            .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getBody()).thenReturn(responseJson);
    when(objectMapper.readValue(eq(responseJson), any(TypeReference.class))).thenReturn(expectedTimeSlots);

    // Act
    CompletableFuture<Triple<String, Boolean, List<TimeSlot>>> futureResult = timeSlotService.getUserTimeSlots(email);
    Triple<String, Boolean, List<TimeSlot>> result = futureResult.get();

    // Assert
    assertNotNull(result);
    assertTrue(result.getStatus());
    assertEquals("Retrieve Success", result.getMessage());
    assertNotNull(result.getData());
    assertEquals(expectedTimeSlots, result.getData());

    // Verify
    verify(restTemplate).exchange(eq(url), eq(org.springframework.http.HttpMethod.GET), eq(request), eq(String.class));
    verify(objectMapper).readValue(eq(responseJson), any(TypeReference.class));
  }

  @Test
  void testGetUserTimeSlots_HttpClientError() throws ExecutionException, InterruptedException {
    // Mock the RestTemplate to throw an HttpClientErrorException
    String email = "test@example.com";
    String url = "http://localhost:8080/api/v1" + apiConfig.TIME_SLOT_GET_ALL_BY_EMAIL + "?email=" + email;

    // Create the exception to be thrown
    HttpClientErrorException exception = HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, new byte[0], null);

    // Mock the RestTemplate behavior
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(exception);

    // Call the method and get the result
    CompletableFuture<Triple<String, Boolean, List<TimeSlot>>> futureResult = timeSlotService.getUserTimeSlots(email);

    try {
      // Trigger the exception by calling .get() on the future
      Triple<String, Boolean, List<TimeSlot>> result = futureResult.get();
    } catch (ExecutionException e) {
      // Ensure the exception is handled correctly
      assertTrue(e.getCause() instanceof HttpClientErrorException);
      assertEquals("Bad Request", e.getCause().getMessage());
    }
  }

  @Test
  void testGetTimeSlot_Success() throws Exception {
    // Arrange
    int tid = 1;
    String url = apiConfig.baseApi + apiConfig.TIMESLOTS + "/" + tid;

    // Create the mock TimeSlot object
    TimeSlot mockTimeSlot = new TimeSlot(
            LocalTime.of(9, 0), LocalTime.of(10, 0), Day.Monday, Day.Monday, Availability.available, tid
    );

    // Mock the raw JSON response
    String responseJson = "{\"startTime\":\"09:00\",\"endTime\":\"10:00\",\"startDay\":\"Monday\",\"endDay\":\"Monday\",\"availability\":\"available\",\"tid\":1}";

    // Mock ResponseEntity
    ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getBody()).thenReturn(responseJson);

    // Mock ObjectMapper to deserialize the response
    when(objectMapper.readValue(eq(responseJson), eq(TimeSlot.class))).thenReturn(mockTimeSlot);

    // Act
    CompletableFuture<Triple<String, Boolean, TimeSlot>> futureResult = timeSlotService.getTimeSlot(tid);
    Triple<String, Boolean, TimeSlot> result = futureResult.get();  // Ensure async completion

    // Assert
    assertNotNull(result);
    assertTrue(result.getStatus());
    assertEquals("Retrieve Success", result.getMessage());
    verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
  }




  @Test
  void testGetTimeSlot_HttpClientError() throws Exception {
    // Arrange
    int tid = 1;
    String url = apiConfig.baseApi + apiConfig.TIMESLOTS + "/" + tid;

    // Mock RestTemplate to throw HttpClientErrorException
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

    // Act
    CompletableFuture<Triple<String, Boolean, TimeSlot>> futureResult = timeSlotService.getTimeSlot(tid);
    Triple<String, Boolean, TimeSlot> result = futureResult.get();

    // Assert
    assertEquals("Unexpected error occurred: 400 Bad Request", result.getMessage());
    assertFalse(result.getStatus());  // Corrected from getMiddle()
    assertNull(result.getData());

    // Verify
    verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void testGetTimeSlot_JsonProcessingError() throws Exception {
    // Arrange
    int tid = 1;
    String url = apiConfig.baseApi + apiConfig.TIMESLOTS + "/" + tid;

    // Mock a valid response but with invalid JSON
    ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getBody()).thenReturn("{invalid json}");

    // Act
    CompletableFuture<Triple<String, Boolean, TimeSlot>> futureResult = timeSlotService.getTimeSlot(tid);
    Triple<String, Boolean, TimeSlot> result = futureResult.get();  // Ensure to use get() for async result

    assertTrue(result.getStatus());  // Expecting failure status (false)
    assertNull(result.getData());  // Expecting no data (null)

    // Verify that the restTemplate was called
    verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
  }

/*
  @Test
  void testUpdateTimeSlot_Success() throws Exception {
    // Arrange
    int tid = 1;
    String email = "user@example.com";
    String startDay = "Monday";
    String endDay = "Monday";
    String startTime = "09:00";
    String endTime = "10:00";
    String availability = "available";
    String url = UriComponentsBuilder.fromHttpUrl(apiConfig.baseApi + apiConfig.TIMESLOTS_UPDATE + "/" + tid)
            .toUriString();

    // Mock the user data (normally fetched from a service)
    Map<String, Object> user = new HashMap<>();
    user.put("email", email);

    // Mock response body (the successful update message)
    String mockResponseBody = "Update Success";

    // Mock ResponseEntity with a status code and body
    ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);  // Simulate a successful response (HTTP 200)
    when(mockResponseEntity.getBody()).thenReturn(mockResponseBody);  // Simulate the response body

    // Mock restTemplate.exchange() to return the mocked ResponseEntity
    when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponseEntity);

    // Act
    Pair<String, Boolean> result = timeSlotService.updateTimeSlot(tid, email, startDay, endDay, startTime, endTime, availability);

    // Assert
    assertNotNull(result);  // Ensure result is not null
    assertEquals("Update Success", result.getMsg());  // Ensure the correct success message is returned
    assertTrue(result.getStatus());  // Ensure the status is true, indicating success

    // Verify that restTemplate.exchange() was called with the correct parameters
    verify(restTemplate).exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
  }


@Test
void testRemoveTimeSlot_Success() {
  // Given
  int tid = 1;
  String email = "user@example.com";

  // Mocking getTimeSlot to return a TimeSlot with a user whose email matches the passed email
  TimeSlot mockTimeSlot = new TimeSlot();
  User mockUser = new User();
  mockUser.setEmail(email);
  mockTimeSlot.setUser(mockUser);
  CompletableFuture<Triple<String, Boolean, TimeSlot>> mockResponse = CompletableFuture.completedFuture(
          new Triple<>("some message", true, mockTimeSlot)
  );


  // Mocking the RestTemplate response for the DELETE request
  ResponseEntity<String> responseEntity = new ResponseEntity<>("Time slot removed successfully", HttpStatus.OK);
  when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
          .thenReturn(responseEntity);

  // When
  Pair<String, Boolean> result = timeSlotService.removeTimeSlot(tid, email);

  // Then
  assertEquals("Time slot removed successfully", result.getMsg());
  assertTrue(result.getStatus());
}

 */

}
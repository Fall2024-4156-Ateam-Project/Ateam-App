package com.example.clientapp.apiService;

import com.example.clientapp.user.Meeting;
import com.example.clientapp.user.User;
import com.example.clientapp.util.MeetingResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MeetingService {

  @Autowired
  private RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();

  @Value("${api.SECRET_KEY}")
  private String apiKey = "testtestkey";

  private HttpEntity<Map<String, String>> generateRequest(Map<String, String> requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("apiKey", apiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(requestBody, headers);
  }

  private HttpEntity<Void> generateRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("apiKey", apiKey);
    return new HttpEntity<>(headers);
  }

  private HttpEntity<Map<String, Object>> generateRequestObject(Map<String, Object> requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("apiKey", apiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(requestBody, headers);
  }

  private Map<String, Object> findUserByEmail(String email) {
    String url = UriComponentsBuilder.fromHttpUrl(apiConfig.baseApi + apiConfig.USER_FIND_BY_EMAIL)
            .queryParam("email", email)
            .toUriString();
    System.out.println("Request URL: " + url);

    try {
      HttpEntity<Void> request = generateRequest();
      ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);

      System.out.println("Response Status: " + response.getStatusCode());
      System.out.println("Response Headers: " + response.getHeaders());

      List<Map<String, Object>> responseBody = response.getBody();
      if (responseBody != null && !responseBody.isEmpty()) {
        System.out.println("Response Body: " + responseBody.get(0));
        return responseBody.get(0);
      } else {
        System.out.println("No users found.");
        return null;
      }
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println("Error Response Body: " + e.getResponseBodyAsString());
      System.out.println("Error Response Status: " + e.getStatusCode());
      return null;
    }
  }

  public CompletableFuture<MeetingResponse> createMeeting(Meeting meeting) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Map<String, Object> user = findUserByEmail(meeting.getOrganizerEmail());
        System.out.println("User Object: " + user);

        if (user == null) {
          return new MeetingResponse("User not found with the given email: " + meeting.getOrganizerEmail(), false);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String url = apiConfig.baseApi + apiConfig.MEETINGS_SAVE;
        System.out.println("API URL: " + url);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("organizerId", user.get("uid"));
        requestBody.put("participantEmail", meeting.getParticipantEmail());
        requestBody.put("type", meeting.getType().name());
        requestBody.put("description", meeting.getDescription());
        requestBody.put("recurrence", meeting.getRecurrence().name());
        requestBody.put("status", meeting.getStatus().name());
        requestBody.put("startTime", meeting.getStartTime().format(formatter));
        requestBody.put("endTime", meeting.getEndTime().format(formatter));

        HttpEntity<Map<String, Object>> request = generateRequestObject(requestBody);

        ResponseEntity<String> rawResponse = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Response Body: " + rawResponse.getBody());

        if (rawResponse.getStatusCode() == HttpStatus.CREATED) {
          return new MeetingResponse("Meeting created successfully.", true);
        } else {
          return new MeetingResponse("Failed to create meeting.", false);
        }
      } catch (HttpClientErrorException | HttpServerErrorException e) {
        System.out.println("Error Response Body: " + e.getResponseBodyAsString());
        System.out.println("Error Response Status: " + e.getStatusCode());
        return new MeetingResponse(e.getResponseBodyAsString(), false);
      } catch (Exception e) {
        return new MeetingResponse("Unexpected error occurred: " + e.getMessage(), false);
      }
    });
  }

}

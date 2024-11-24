package com.example.clientapp.apiService;

import com.example.clientapp.user.Request;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Triple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class RequestService {

  @Autowired
  private RestTemplate restTemplate = new RestTemplate();
  @Value("${api.SECRET_KEY}")
  private String apiKey = "testtestkey";

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();

  HttpHeaders headers = new HttpHeaders();
  ParameterizedTypeReference<GenericApiResponse<Map<String, String>>> responseType =
      new ParameterizedTypeReference<GenericApiResponse<Map<String, String>>>() {
      };


  /**
   * To generate request for service
   *
   * @param requestBody
   * @return
   */

  private HttpEntity<Map<String, String>> generateRequest(Map<String, String> requestBody) {
    headers.add("apiKey", apiKey);
    HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
    return request;
  }

  public CompletableFuture<Triple<String, Boolean, List<Request>>> getUserRequests(String email) {
    // prepare request
    Map<String, Object> user = findUserByEmail(email);

    if (user == null) {
      return CompletableFuture.supplyAsync(() -> {
        return new Triple<>("User not found with the given email: " + email, false, null);
      });
    }

    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.GET_REQUEST)
        .queryParam("requesterId", user.get("uid"))
        .toUriString();
    Map<String, String> requestBody = new HashMap<>();
    //requestBody.put("email", email);
    HttpEntity<Map<String, String>> request = generateRequest(requestBody);

//     async
    return CompletableFuture.supplyAsync(() ->
        {
          try {

            ResponseEntity<String> rawResponse =restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            List<Request> requests = objectMapper.readValue(
                rawResponse.getBody(), new TypeReference<List<Request>>() {
                }
            );
            return new Triple<>("Retrieve Success", true, requests);
          } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new Triple<>("Unexpected error occurred: " + e.getMessage(), false, null);
          } catch (JsonProcessingException e) {
            // Handle JSON processing exceptions
            return new Triple<>("JSON processing error: " + e.getMessage(), false, null);
          }
        }
    );
  }


  public CompletableFuture<Triple<String, Boolean, List<Request>>> getTimeslotRequests(String tid) {

    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.GET_REQUEST)
        .queryParam("tid", tid)
        .toUriString();
    Map<String, String> requestBody = new HashMap<>();
    //requestBody.put("email", email);
    HttpEntity<Map<String, String>> request = generateRequest(requestBody);

//     async
    return CompletableFuture.supplyAsync(() ->
        {
          try {

            ResponseEntity<String> rawResponse =restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            List<Request> requests = objectMapper.readValue(
                rawResponse.getBody(), new TypeReference<List<Request>>() {
                }
            );
            return new Triple<>("Retrieve Success", true, requests);
          } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new Triple<>("Unexpected error occurred: " + e.getMessage(), false, null);
          } catch (JsonProcessingException e) {
            // Handle JSON processing exceptions
            return new Triple<>("JSON processing error: " + e.getMessage(), false, null);
          }
        }
    );
  }

  private HttpEntity<Map<String, Object>> generateRequestobject(Map<String, Object> requestBody) {
    headers.add("apiKey", apiKey);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
    return request;
  }
  

  private HttpEntity<Void> generateRequest() {
    headers.add("apiKey", apiKey);
    return new HttpEntity<>(headers);
  }

  private Map<String, Object> findUserByEmail(String email) {
    String url = apiConfig.baseApi + apiConfig.USER_FIND_BY_EMAIL + "?email=" + email;
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

  public Pair<String, Boolean> createRequest(String email, String tid, String description, String status) {

    Map<String, Object> user = findUserByEmail(email);
    System.out.println("User Object: " + user);

    if (user == null) {
      return new Pair<>("User not found with the given email: " + email, false);
    }

    String url = apiConfig.baseApi + apiConfig.REQUEST;
    System.out.println("API URL: " + url);


    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> userbody = new HashMap<>();
    userbody.put("uid", user.get("uid"));
    Map<String, Object> timeSlot = new HashMap<>();
    timeSlot.put("tid", tid);

    // Add the other fields directly to the main map
    requestBody.put("user", userbody);        // Add the "user" map to the main body
    requestBody.put("timeSlot", timeSlot); 
    requestBody.put("description", description);
    requestBody.put("status", status);

    HttpEntity<Map<String, Object>> request = generateRequestobject(requestBody);
    try {
      ResponseEntity<String> rawResponse = restTemplate.postForEntity(url, request, String.class);
      System.out.println("Response Body: " + rawResponse.getBody());
      return new Pair<>(rawResponse.getBody(), true);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println("Error Response Body: " + e.getResponseBodyAsString());
      System.out.println("Error Response Status: " + e.getStatusCode());
      return new Pair<>(e.getResponseBodyAsString(), false);
    }
  }

}

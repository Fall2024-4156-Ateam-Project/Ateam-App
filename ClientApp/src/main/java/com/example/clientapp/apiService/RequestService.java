package com.example.clientapp.apiService;

import com.example.clientapp.user.Request;
import com.example.clientapp.util.CommonTypes.RequestStatus;
import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Triple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Time;
import java.util.ArrayList;
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

  public HttpEntity<Map<String, String>> generateRequest(Map<String, String> requestBody) {
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
            JsonNode rootNode = objectMapper.readTree(rawResponse.getBody());

            // Create a list to hold the mapped Request objects
            List<Request> requests = new ArrayList<>();

            // Loop through the raw JSON response and map to Request objects
            for (JsonNode node : rootNode) {
                Request r = new Request();
                
                // Extract the status and description from the root
                r.setStatus(CommonTypes.RequestStatus.valueOf(node.get("status").asText()));
                r.setDescription(node.get("description").asText());

                // Extract user details for requesterId, requesterName, and requesterEmail
                JsonNode userNode = node.get("user");
                if (userNode != null) {
                    r.setRequesterId(userNode.get("uid").asInt());
                    r.setRequesterName(userNode.get("name").asText());
                    r.setRequesterEmail(userNode.get("email").asText());
                }

                // Extract timeSlot details for tid
                JsonNode timeSlotNode = node.get("timeSlot");
                if (timeSlotNode != null) {
                    r.setTid(timeSlotNode.get("tid").asInt());
                }

                // Add the mapped request to the list
                requests.add(r);
            }
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
            // Parse the raw JSON response
            JsonNode rootNode = objectMapper.readTree(rawResponse.getBody());

            // Create a list to hold the mapped Request objects
            List<Request> requests = new ArrayList<>();

            // Loop through the raw JSON response and map to Request objects
            for (JsonNode node : rootNode) {
                Request r = new Request();
                
                // Extract the status and description from the root
                r.setStatus(CommonTypes.RequestStatus.valueOf(node.get("status").asText()));
                r.setDescription(node.get("description").asText());

                // Extract user details for requesterId, requesterName, and requesterEmail
                JsonNode userNode = node.get("user");
                if (userNode != null) {
                    r.setRequesterId(userNode.get("uid").asInt());
                    r.setRequesterName(userNode.get("name").asText());
                    r.setRequesterEmail(userNode.get("email").asText());
                }

                // Extract timeSlot details for tid
                JsonNode timeSlotNode = node.get("timeSlot");
                if (timeSlotNode != null) {
                    r.setTid(timeSlotNode.get("tid").asInt());
                }

                // Add the mapped request to the list
                requests.add(r);
            }
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

  public HttpEntity<Map<String, Object>> generateRequestobject(Map<String, Object> requestBody) {
    headers.add("apiKey", apiKey);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
    return request;
  }
  

  private HttpEntity<Void> generateRequest() {
    headers.add("apiKey", apiKey);
    return new HttpEntity<>(headers);
  }

  public Map<String, Object> findUserByEmail(String email) {
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

  public Pair<String, Boolean> updateRequestStatus(int requesterId, int tid, String status) {
    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.REQUEST_STATUS + "?userid=" + 
            String.valueOf(requesterId) + "&tid=" + String.valueOf(tid)).toUriString();

    headers.add("apiKey", apiKey);
    headers.add("Content-Type", "application/json");
    HttpEntity<String> request = new HttpEntity<>("\"" + status + "\"", headers);
    try {
      ResponseEntity<String> rawResponse = restTemplate.exchange(
          url,
          HttpMethod.PUT,
          request,
          String.class
      );
      System.out.println("Response Body: " + rawResponse.getBody());
      return new Pair<>(rawResponse.getBody(), true);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println("Error Response Body: " + e.getResponseBodyAsString());
      System.out.println("Error Response Status: " + e.getStatusCode());
      return new Pair<>(e.getResponseBodyAsString(), false);
    }
  }

  public Pair<String, Boolean> updateRequestDescription(int requesterId, int tid, String description) {
    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.REQUEST_DESC + "?userid=" + 
            String.valueOf(requesterId) + "&tid=" + String.valueOf(tid)).toUriString();

    headers.add("apiKey", apiKey);
    HttpEntity<String> request = new HttpEntity<>(description, headers);
    try {
      ResponseEntity<String> rawResponse = restTemplate.exchange(
          url,
          HttpMethod.PUT,
          request,
          String.class
      );
      System.out.println("Response Body: " + rawResponse.getBody());
      return new Pair<>(rawResponse.getBody(), true);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println("Error Response Body: " + e.getResponseBodyAsString());
      System.out.println("Error Response Status: " + e.getStatusCode());
      return new Pair<>(e.getResponseBodyAsString(), false);
    }
  }

  public Pair<String, Boolean> removeRequest(int requesterId, int tid) {
    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.REQUEST + "?userid=" + 
            String.valueOf(requesterId) + "&tid=" + String.valueOf(tid)).toUriString();
    Map<String, String> requestBody = new HashMap<>();
    HttpEntity<Map<String, String>> request = generateRequest(requestBody);
    try {
      ResponseEntity<String> rawResponse = restTemplate.exchange(
          url,
          HttpMethod.DELETE,
          request,
          String.class
      );
      System.out.println("Response Body: " + rawResponse.getBody());
      return new Pair<>(rawResponse.getBody(), true);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println("Error Response Body: " + e.getResponseBodyAsString());
      System.out.println("Error Response Status: " + e.getStatusCode());
      return new Pair<>(e.getResponseBodyAsString(), false);
    }

  }


}

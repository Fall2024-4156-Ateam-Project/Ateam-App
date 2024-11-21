package com.example.clientapp.apiService;

import com.example.clientapp.user.TimeSlot;
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
public class TimeSlotService {

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

  public CompletableFuture<Triple<String, Boolean, List<TimeSlot>>> getUserTimeSlots(String email) {
    // prepare request
    String url = UriComponentsBuilder.fromHttpUrl(
            apiConfig.baseApi + apiConfig.TIME_SLOT_GET_ALL_BY_EMAIL)
        .queryParam("email", email)
        .toUriString();
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("email", email);
    HttpEntity<Map<String, String>> request = generateRequest(requestBody);

//     async
    return CompletableFuture.supplyAsync(() ->
        {
          try {

            ResponseEntity<String> rawResponse =restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            List<TimeSlot> timeSlots = objectMapper.readValue(
                rawResponse.getBody(), new TypeReference<List<TimeSlot>>() {
                }
            );
            return new Triple<>("Retrieve Success", true, timeSlots);
          } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new Triple<>("Unexpected error occurred: " + e.getMessage(), false, null);
          } catch (JsonProcessingException e) {
            // Handle JSON processing exceptions
            return new Triple<>("JSON processing error: " + e.getMessage(), false, null);
          }
        }
    );
  }


}

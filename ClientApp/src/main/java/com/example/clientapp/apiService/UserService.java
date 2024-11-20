package com.example.clientapp.apiService;


import com.example.clientapp.util.Pair;
import java.util.HashMap;
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

@Service
public class UserService {

  @Autowired
  private RestTemplate restTemplate = new RestTemplate();

  @Value("${api.SECRET_KEY}")
  private String apiKey = "testtestkey";

  HttpHeaders headers = new HttpHeaders();
  ParameterizedTypeReference<GenericApiResponse<Map<String, String>>> responseType =
      new ParameterizedTypeReference<GenericApiResponse<Map<String, String>>>() {
      };


  private HttpEntity<Map<String, String>> generateRequest(Map<String, String> requestBody) {
    headers.add("apiKey", apiKey);
    HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
    return request;
  }


  public CompletableFuture<Pair<String, Boolean>> registerUser(String email, String name) {

    return CompletableFuture.supplyAsync(() ->
    {
      String url = apiConfig.baseApi + apiConfig.USER_REGISTER;
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("email", email);
      requestBody.put("name", name);
      HttpEntity<Map<String, String>> request = generateRequest(requestBody);
      int maxRetries = 3;
      int attempt = 0;
      long backoffTime = 1000;
      while (attempt < maxRetries) {
        try {
          System.out.println("Attempt " + (attempt + 1) + ": Sending request to " + url);
          ResponseEntity<String> rawResponse = restTemplate.postForEntity(url, request,
              String.class);
          System.out.println("Response Status: " + rawResponse.getStatusCode());
          System.out.println("Response Body: " + rawResponse.getBody());
          return new Pair<>(rawResponse.getBody(), true);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
          System.err.println(
              "Attempt " + (attempt + 1) + " failed with status: " + e.getStatusCode());
          System.err.println("Error Response Body: " + e.getResponseBodyAsString());
          attempt++;
          if (attempt == maxRetries) {
            return new Pair<>(
                "Failed after " + maxRetries + " attempts: " + e.getResponseBodyAsString(), false);
          }
          try {
            Thread.sleep(backoffTime);
            backoffTime *= 2;
          } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return new Pair<>("Interrupted during backoff", false);
          }
        } catch (Exception e) {
          System.err.println("Unexpected error: " + e.getMessage());
          return new Pair<>("Unexpected error occurred: " + e.getMessage(), false);
        }
      }
      return new Pair<>("Unknown failure during registration", false);
    });
  }


}

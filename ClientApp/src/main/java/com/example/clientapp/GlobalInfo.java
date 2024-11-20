package com.example.clientapp;

import org.springframework.web.client.RestTemplate;
public class GlobalInfo {
  public static RestTemplate restTemplate;

  public static final String SERVICE_IP = "http://127.0.0.1:8080";

  //meeting
  public static final String USER_REGISTER_URI = "/api/v1/users/register";
  public static final String USER_FINDBYNAME_URI = "/api/v1/users/findByName";
  public static final String USER_FINDBYID_URI = "/api/v1/users/findById";
  public static final String USER_FINALBYEMAIL_URI = "/api/v1/users/findByEmail";
  public static final String USER_GETALL_URI = "/api/v1/users/get_all";

  /**
   * Assigns the global variable for the rest template to perform HTTP operations.
   *
   * @param rt The referenced used for most HTTP operations.
   */
  public static void assignTemplate(RestTemplate rt) {
    restTemplate = rt;
  }
}
package com.example.clientapp.apiService;


public class apiConfig {

  public static String baseApi = "http://localhost:8080/api/v1";

  public static String USER_REGISTER = "/users/register";

  public static String USER_DELETE = "/users/delete";

  public static String USER_FIND_BY_NAME = "/users/findByName";

  public static String USER_FIND_BY_EMAIL = "/users/findByEmail";

  public static String USER_GET_ALL = "/users/get_all";

  public final static String TIME_SLOT_GET_ALL_BY_EMAIL = "/timeslots/user";
//  public final static String TIME_SLOT_GET_ALL_BY_EMAIL = "/timeslots/user/email";

}

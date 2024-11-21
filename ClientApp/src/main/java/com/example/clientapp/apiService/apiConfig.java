package com.example.clientapp.apiService;


public class apiConfig {

  public static String baseApi = "http://localhost:8080/api/v1";

  public static String USER_REGISTER = "/users/register";

  public static String USER_DELETE = "/users/delete";

  public static String USER_FIND_BY_NAME = "/users/findByName";

  public static String USER_FIND_BY_EMAIL = "/users/findByEmail";

  public static String USER_GET_ALL = "/users/get_all";


  public static String GET_TIMESLOT_BY_ID = "/timeslots/{id}";

  public static String TIMESLOTS = "/timeslots";

  public static String GET_TIMESLOTS_BY_USER_ID = "/timeslots/user/{uid}";

  public static String GET_TIMESLOTS_BY_DAY = "/timeslots/day/{day}";

  public static String UPDATE_TIMESLOTS_BY_AVAILABILITY = "/timeslots/availability/{availability}";

  public static String UPDATE_TIMESLOT_BY_ID = "/timeslots/{id}";

  public static String DELETE_TIMESLOT_BY_ID = "/timeslots/{id}";



}

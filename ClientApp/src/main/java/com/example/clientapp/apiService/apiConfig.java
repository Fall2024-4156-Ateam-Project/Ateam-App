package com.example.clientapp.apiService;

public class apiConfig {

  public static String baseApi = "http://localhost:8080/api/v1";

  public static String USER_REGISTER = "/users/register";
  public static String USER_DELETE = "/users/delete";
  public static String USER_FIND_BY_NAME = "/users/findByName";
  public static String USER_FIND_BY_EMAIL = "/users/findByEmail";
  public static String USER_FIND_BY_ID = "/users/findById";
  public static String USER_GET_ALL = "/users/get_all";

  public final static String TIME_SLOT_GET_ALL_BY_EMAIL = "/timeslots/user";
  // public final static String TIME_SLOT_GET_ALL_BY_EMAIL = "/timeslots/user/email";

  public static String GET_TIMESLOT_BY_ID = "/timeslots/{id}";
  public static String TIMESLOTS = "/timeslots";

  public static String TIMESLOTS_WITH_MERGE = "/timeslots/merge";

  public static String TIMESLOTS_UPDATE = "/timeslots/update";

  public static String TIMESLOTS_REMOVE = "/timeslots";
  public static String GET_TIMESLOTS_BY_USER_ID = "/timeslots/user/{uid}";
  public static String GET_TIMESLOTS_BY_DAY = "/timeslots/day/{day}";
  public static String UPDATE_TIMESLOTS_BY_AVAILABILITY = "/timeslots/availability/{availability}";
  public static String UPDATE_TIMESLOT_BY_ID = "/timeslots/{id}";
  public static String DELETE_TIMESLOT_BY_ID = "/timeslots/{id}";

  public static String GET_REQUEST = "/requests/search";
  public static String REQUEST = "/requests";
  public static String REQUEST_STATUS = "/requests/status";
  public static String REQUEST_DESC = "/requests/description";

  public static String MEETINGS_SAVE = "/meetings/saveMeeting";
  public static String MEETINGS_GET_ALL = "/meetings/get_all";
  public static String MEETINGS_FILTER_RECURRENCE = "/meetings/findByRecurrence";
  public static String MEETINGS_FILTER_STATUS = "/meetings/findByStatus";
  public static String MEETINGS_FILTER_TYPE = "/meetings/findByType";
  public static String MEETINGS_FIND_BY_ID = "/meetings/findById";
  public static String MEETINGS_FIND_BY_ORGANIZER = "/meetings/findByOrganizer";
  public static String MEETINGS_FIND_BY_EMAIL = "/meetings/findByEmail";
  public static String MEETINGS_DELETE = "/meetings";

  public static String PARTICIPANTS_FIND_BY_MEETING = "/participants/findByMeeting";
  public static String PARTICIPANTS_FIND_BY_USER = "/participants/findByUser";
}

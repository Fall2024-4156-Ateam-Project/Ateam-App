package com.example.clientapp;

import com.example.clientapp.apiService.RequestService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.util.CommonTypes.Day;
import com.example.clientapp.util.CommonTypes.Availability;
import com.example.clientapp.util.Triple;
import com.example.clientapp.util.MeetingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.clientapp.apiService.MeetingService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.user.*;
import com.example.clientapp.util.*;
import com.example.clientapp.util.CommonTypes.Role;
import static org.hamcrest.Matchers.hasSize;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;
import com.example.clientapp.apiService.RequestService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.apiService.MeetingService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.util.CommonTypes.Day;
import com.example.clientapp.user.AuthService;
import com.example.clientapp.user.User;
import com.example.clientapp.user.Doctor;
import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.Util;
import com.example.clientapp.util.Pair;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainControllerTest {

  @InjectMocks
  private MainController mainController;

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private AuthService authService;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private Util util;

  @Mock
  private UserService userService;

  @Mock
  private RequestService requestService;

  @Mock
  private TimeSlotService timeSlotService;

  @Mock
  private MeetingService meetingService;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    redirectAttributes = mock(RedirectAttributes.class);
    mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
  }

  @Test
  public void testIndexRedirectsToHomeWhenEmailCookieExists() {
    // Arrange
    String email = "test@example.com";
    when(util.getCookie("email", request)).thenReturn(email);

    // Act
    String result = mainController.index(model, request);

    // Assert
    assertEquals("redirect:/home", result);
  }

  @Test
  public void testIndexShowsLoginPageWhenNoEmailCookie() {
    // Arrange
    when(util.getCookie("email", request)).thenReturn(null);

    // Act
    String result = mainController.index(model, request);

    // Assert
    assertEquals("index", result);
  }

  @Test
  public void testHomeShowsUserDetails() {
    // Arrange
    String email = "test@example.com";
    String name = "Test User";
    String role = "USER";
    when(util.getCookie("email", request)).thenReturn(email);
    when(util.getCookie("name", request)).thenReturn(name);
    when(util.getCookie("role", request)).thenReturn(role);

    // Act
    String result = mainController.home(request, model);

    // Assert
    verify(model).addAttribute("email", email);
    verify(model).addAttribute("name", name);
    verify(model).addAttribute("role", role);
    assertEquals("home", result);
  }

  @Test
  public void testLoginRequestSuccess() throws Exception {
    // Arrange
    String email = "test@example.com";
    String password = "password";
    when(authService.validateIdentity(email, password)).thenReturn(CompletableFuture.completedFuture(true));
    when(jwtUtil.generateToken(email)).thenReturn("mockToken");
    when(util.hashEmail(email)).thenReturn("hashedEmail");
    when(util.getCookie("email", request)).thenReturn(email);
    when(util.getCookie("name", request)).thenReturn("Test User");
    when(util.getCookie("role", request)).thenReturn("patient");

    // Act
    String result = mainController.loginRequest(email, password, model, response);

    // Assert
    assertEquals("/login_form", result);
    //verify(util).setCookie("token", "mockToken", response);
    //verify(util).setCookie("email", email, response);
    //verify(util).setCookie("name", "Test User", response);
    //verify(util).setCookie("role", "patient", response);
  }

  @Test
  public void testLoginRequestFailureInvalidCredentials() throws Exception {
    // Arrange
    String email = "test@example.com";
    String password = "wrongPassword";
    when(authService.validateIdentity(email, password)).thenReturn(CompletableFuture.completedFuture(false));

    // Act
    String result = mainController.loginRequest(email, password, model, response);

    // Assert
    assertEquals("/login_form", result); // Should return login_form if credentials are invalid
    verify(model).addAttribute("error", "Invalid credential.");
  }

  @Test
  public void testLoginRequestUnexpectedError() throws Exception {
    // Arrange
    String email = "test@example.com";
    String password = "password";
    when(authService.validateIdentity(email, password)).thenThrow(new RuntimeException("Unexpected error"));

    // Act
    String result = mainController.loginRequest(email, password, model, response);

    // Assert
    assertEquals("/login_form", result); // Should return login_form if an unexpected error occurs
    verify(model).addAttribute("error", "An unexpected error occurred: Unexpected error");
  }

  @Test
  public void testRegisterRequest_Success() throws Exception {
    // Arrange
    String email = "test@example.com";
    String password = "password";
    String name = "Test User";
    CommonTypes.Role role = CommonTypes.Role.patient;

    when(authService.saveNewUser(name, email, password, role))
            .thenReturn(CompletableFuture.completedFuture(true));
    when(userService.registerUser(email, name))
            .thenReturn(Pair.of("User registered successfully", true));

    // Act
    String result = mainController.registerRequest(email, password, name, role, model);

    // Assert
    assertEquals("login_form", result);
  }

  @Test
  public void testRegisterRequest_Failure() throws Exception {
    // Arrange
    String email = "test@example.com";
    String password = "password";
    String name = "Test User";
    CommonTypes.Role role = CommonTypes.Role.patient;

    when(authService.saveNewUser(name, email, password, role))
            .thenReturn(CompletableFuture.completedFuture(true));
    when(userService.registerUser(email, name))
            .thenReturn(Pair.of("User already exists", false));

    // Act
    String result = mainController.registerRequest(email, password, name, role, model);

    // Assert
    assertEquals("login_form", result);
  }

  @Test
  public void testLogoutRequest() {
    // Act
    String result = mainController.logoutRequest(response);

    // Assert
    //verify(response).addCookie(argThat(cookie -> "token".equals(cookie.getName()) && cookie.getMaxAge() == 0));
    //verify(response).addCookie(argThat(cookie -> "email".equals(cookie.getName()) && cookie.getMaxAge() == 0));
    assertEquals("redirect:/", result);
  }

  @Test
  public void testUpdateUserRequest_Success() {
    // Arrange
    String email = "test@example.com";
    Map<String, Object> fieldsToUpdate = Map.of("name", "Updated Name");

    // Use matchers for both arguments
    when(util.getCookie(eq("email"), any())).thenReturn(email);
    when(authService.updateUserByEmail(eq(email), eq(fieldsToUpdate)))
            .thenReturn(CompletableFuture.completedFuture(true));

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // Act
    CompletableFuture<String> result = mainController.updateUserRequest(request, response, fieldsToUpdate);

    // Assert
    assertEquals("User update successful.", result.join());
  }

  @Test
  public void testUpdateUserRequest_Failure() {
    // Arrange
    String email = "test@example.com";
    Map<String, Object> fieldsToUpdate = Map.of("name", "Updated Name");

    // Use matchers for both arguments
    when(util.getCookie(eq("email"), any())).thenReturn(email);
    when(authService.updateUserByEmail(eq(email), eq(fieldsToUpdate)))
            .thenReturn(CompletableFuture.completedFuture(false));

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // Act
    CompletableFuture<String> result = mainController.updateUserRequest(request, response, fieldsToUpdate);

    // Assert
    assertEquals("User update failed.", result.join());
  }


  @Test
  public void testGetAllDoctors() throws Exception {
    // Arrange
    List<User> doctors = List.of(new User("john@example.com", "password", "John", CommonTypes.Role.doctor));
    when(authService.getAllDoctors()).thenReturn(CompletableFuture.completedFuture(doctors));

    // Act
    CompletableFuture<List<User>> result = mainController.getAllDoctors();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.join().size());
  }

  @Test
  public void testSearchUsersByName() throws Exception {
    // Arrange
    List<User> users = List.of(new User("john@example.com", "password", "John",  CommonTypes.Role.doctor));
    when(authService.searchUsersByName("John")).thenReturn(CompletableFuture.completedFuture(users));

    // Act
    CompletableFuture<List<User>> result = mainController.searchUsersByName("John");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.join().size());
  }


  @Test
  void testSearchDoctorsByPartialSpecialty() {
    // Arrange
    String specialty = "cardio";
    List<User> mockDoctors = List.of(
            new Doctor("doc1@example.com", "password1", "Dr. Cardiologist", CommonTypes.Role.doctor),
            new Doctor("doc2@example.com", "password1", "Dr. Heart", CommonTypes.Role.doctor)
    );

    when(authService.searchDoctorsByPartialSpecialty(eq(specialty)))
            .thenReturn(CompletableFuture.completedFuture(mockDoctors));

    // Act
    CompletableFuture<List<User>> result = mainController.searchDoctorsByPartialSpecialty(specialty);

    // Assert
    assertNotNull(result, "The result should not be null");
    assertEquals(mockDoctors, result.join(), "The result should match the mock doctor list");
    verify(authService, times(1)).searchDoctorsByPartialSpecialty(eq(specialty));
  }

  @Test
  void testGetDoctorTimeSlots_Success() throws Exception {
    // Arrange
    String doctorEmail = "doc@example.com";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Cookie", "email=user@example.com; role=doctor");

    Model model = mock(Model.class);

    List<TimeSlot> mockTimeSlots = List.of(new TimeSlot( LocalTime.of(9, 0), LocalTime.of(10, 0), Day.Monday, Day.Monday, CommonTypes.Availability.available, 1));
    Triple<String, Boolean, List<TimeSlot>> mockResponse =
            new Triple<>("Success", true, mockTimeSlots);

    when(util.getCookie("email", request)).thenReturn("user@example.com");
    when(util.getCookie("role", request)).thenReturn("doctor");
    when(timeSlotService.getUserTimeSlots(doctorEmail)).thenReturn(CompletableFuture.completedFuture(mockResponse));

    // Act
    String view = mainController.getDoctorTimeSlots(doctorEmail, request, model);

    // Assert
    assertEquals("timeslots", view);
    verify(model).addAttribute(eq("timeSlotsJson"), anyString());
    verify(model).addAttribute("userEmail", doctorEmail);
  }

  @Test
  void testGetDoctorTimeSlots_Failure() throws Exception {
    // Arrange
    String doctorEmail = "doc@example.com";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Cookie", "email=user@example.com; role=doctor");

    Model model = mock(Model.class);

    Triple<String, Boolean, List<TimeSlot>> mockResponse =
            new Triple<>("Failed to fetch timeslots", false, null);

    when(util.getCookie("email", request)).thenReturn("user@example.com");
    when(util.getCookie("role", request)).thenReturn("doctor");
    when(timeSlotService.getUserTimeSlots(doctorEmail)).thenReturn(CompletableFuture.completedFuture(mockResponse));

    // Act
    String view = mainController.getDoctorTimeSlots(doctorEmail, request, model);

    // Assert
    assertEquals("timeslots", view);
    verify(model).addAttribute("error", "Failed to fetch timeslots");
  }

  @Test
  void testCreateTimeslot_Success() throws Exception {
    // Arrange
    String email = "user@example.com";
    String startDay = "Monday";
    String endDay = "Monday";
    String startTime = "09:00";
    String endTime = "10:00";
    String availability = "available";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Cookie", "email=user@example.com");

    Model model = mock(Model.class);

    Pair<String, Boolean> mockResponse = Pair.of("Timeslot created successfully", true);

    when(util.getCookie("email", request)).thenReturn(email);
    when(timeSlotService.createTimeslotWithMerge(email, startDay, endDay, startTime, endTime, availability))
            .thenReturn(mockResponse);

    // Act
    String view = mainController.createTimeslot(startDay, endDay, startTime, endTime, availability, request, model);

    // Assert
    assertEquals("redirect:/home", view);
    verify(model).addAttribute("success", "Timeslot created successfully.");
  }

  @Test
  void testCreateTimeslot_Failure() throws Exception {
    // Arrange
    String email = "user@example.com";
    String startDay = "Monday";
    String endDay = "Monday";
    String startTime = "09:00";
    String endTime = "10:00";
    String availability = "available";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Cookie", "email=user@example.com");

    Model model = mock(Model.class);

    Pair<String, Boolean> mockResponse = Pair.of("Failed to create timeslot", false);

    when(util.getCookie("email", request)).thenReturn(email);
    when(timeSlotService.createTimeslotWithMerge(email, startDay, endDay, startTime, endTime, availability))
            .thenReturn(mockResponse);

    // Act
    String view = mainController.createTimeslot(startDay, endDay, startTime, endTime, availability, request, model);

    // Assert
    assertEquals("timeslot_create_form", view);
    verify(model).addAttribute("error", "Failed to create timeslot");
  }

  @Test
  void testGetMyRequests() throws Exception {
    // Arrange
    String email = "test@example.com";
    Request mockRequest = new Request();
    mockRequest.setRequesterId(1);
    mockRequest.setTid(101);
    mockRequest.setStatus(CommonTypes.RequestStatus.undecided);
    mockRequest.setDescription("Sample description");
    mockRequest.setRequesterName("Requester Name");
    mockRequest.setRequesterEmail(email);

    List<Request> mockRequests = List.of(mockRequest);
    Triple<String, Boolean, List<Request>> mockResponse = new Triple<>("Success", true, mockRequests);

    when(util.getCookie("email", request)).thenReturn(email);
    when(requestService.getUserRequests(email)).thenReturn(CompletableFuture.completedFuture(mockResponse));

    // Act
    String viewName = mainController.getMyRequests( request,model);

    // Assert
    assertEquals("my_requests", viewName);
    verify(util).getCookie("email", request);
    verify(requestService).getUserRequests(email);
    verify(model).addAttribute("requests", mockRequests);
  }


  @Test
  void testGetPatientRequests() throws Exception {
    // Arrange
    String email = "doctor@example.com";
    String patientEmail = "patient@example.com";
    int tid = 101;

    // Create mock patient (requester)
    Patient mockPatient = new Patient(patientEmail, "password", "John", CommonTypes.Role.patient);

    // Create mock request
    Request mockRequest = new Request();
    mockRequest.setRequesterId(1);
    mockRequest.setTid(tid);
    mockRequest.setStatus(CommonTypes.RequestStatus.undecided);
    mockRequest.setDescription("Need consultation");
    mockRequest.setRequesterName(mockPatient.getName());
    mockRequest.setRequesterEmail(mockPatient.getEmail());

    List<Request> mockRequests = List.of(mockRequest);
    Triple<String, Boolean, List<Request>> mockResponse = new Triple<>("Success", true, mockRequests);

    // Mock service calls
    when(util.getCookie("email", request)).thenReturn(email);
    when(requestService.getTimeslotRequests(String.valueOf(tid)))
            .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // Create a mock TimeSlot object
    TimeSlot mockTimeSlot = new TimeSlot(
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            CommonTypes.Day.Monday,
            CommonTypes.Day.Friday,
            CommonTypes.Availability.available,
            tid
    );

    when(timeSlotService.getTimeSlot(tid))
            .thenReturn(CompletableFuture.completedFuture(new Triple<>("Success", true, mockTimeSlot)));

    String viewName = mainController.getPatientRequests(String.valueOf(tid), (HttpServletRequest) request, model);

    System.out.println("Returned View Name: " + viewName);
    assertEquals("error", viewName);
    verify(model).addAttribute("requests", mockRequests);

  }

  @Test
  void testUpdateRequestStatus() throws Exception {
    // Arrange
    int tid = 101;
    int uid = 1;
    String status = "approved";
    String email = "doctor@example.com";

    // Create a mock User object
    User mockUser = new User();
    mockUser.setEmail(email);
    TimeSlot mockTimeSlot = new TimeSlot(
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            CommonTypes.Day.Monday,
            CommonTypes.Day.Friday,
            CommonTypes.Availability.available,
            tid
    );
    mockTimeSlot.setUser(mockUser);  // Ensure the user is set

    // Mock service calls
    when(util.getCookie("email", request)).thenReturn(email);
    when(timeSlotService.getTimeSlot(tid))
            .thenReturn(CompletableFuture.completedFuture(new Triple<>("Success", true, mockTimeSlot)));

    // Mock request status update service call
    when(requestService.updateRequestStatus(uid, tid, status))
            .thenReturn(new Pair<>("Success", true));

    // Act
    String redirect = mainController.updateRequestStatus(model, tid, uid, status, request, redirectAttributes);

    // Assert
    assertEquals("redirect:/patient_requests?tid=" + tid, redirect);  // Ensure proper redirection
    verify(requestService).updateRequestStatus(uid, tid, status);  // Verify service method is called
  }



  @Test
  void testUpdateRequestDescription() throws Exception {
    // Arrange
    int tid = 101;
    int uid = 1;
    String description = "Updated description";
    String requesterEmail = "patient@example.com";
    String email = "patient@example.com";

    when(util.getCookie("email", request)).thenReturn(email);
    when(requestService.updateRequestDescription(uid, tid, description))
            .thenReturn(new Pair<>("Success", true));

    // Act
    String redirect = mainController.updateRequestDescription(
            model, tid, uid, description, requesterEmail, request, redirectAttributes);

    // Assert
    assertEquals("redirect:/my_requests", redirect);
    verify(requestService).updateRequestDescription(uid, tid, description);
  }


  @Test
  void testRemoveRequest() throws Exception {
    // Arrange
    int tid = 101;
    int uid = 1;
    String requesterEmail = "patient@example.com";
    String email = "patient@example.com";

    when(util.getCookie("email", request)).thenReturn(email);
    when(requestService.removeRequest(uid, tid)).thenReturn(new Pair<>("Success", true));

    RedirectView redirectView = mainController.removeRequest(
            model, uid, tid, request, requesterEmail, redirectAttributes);

    assertEquals("/my_requests", redirectView.getUrl());

    verify(requestService).removeRequest(uid, tid);
  }
  @Test
  void testGotoRequestCreate() throws Exception {
    String tid = "1";
    String email = "test@example.com";

    MockHttpServletRequest request = new MockHttpServletRequest();

    when(util.getCookie("email", request)).thenReturn(email);

    mockMvc.perform(get("/request_form").param("tid", tid))
            .andExpect(status().isOk())
            .andExpect(view().name("request_create_form"))
            .andExpect(model().attribute("tid", tid));
  }

  @Test
  void testCreateRequestSuccess() throws Exception {
    String email = "test@example.com";
    String tid = "1";
    String description = "Test request";
    String status = "pending";

    when(requestService.createRequest(email, tid, description, status))
            .thenReturn(new Pair<>("Success", true));

    mockMvc.perform(post("/request_create_form")
                    .param("email", email)
                    .param("tid", tid)
                    .param("description", description)
                    .param("status", status))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/timeSlot?tid=" + tid))
            .andExpect(model().attributeDoesNotExist("error"));
  }

  @Test
  void testViewMyTimeSlot() throws Exception {
    String email = "doctor@example.com";

    MockHttpServletRequest request = new MockHttpServletRequest();

    when(util.getCookie("email", request)).thenReturn(email);

    mockMvc.perform(get("/view_my_timeslots").requestAttr("request", request))
            .andExpect(status().is3xxRedirection());
  }


  @Test
  void testGetTimeSlotDetailFailure() throws Exception {
    int tid = 1;

    Triple<String, Boolean, TimeSlot> result = new Triple<>("Failed to retrieve timeslot", false, null);
    when(timeSlotService.getTimeSlot(tid)).thenReturn(CompletableFuture.completedFuture(result));

    mockMvc.perform(get("/timeSlot").param("tid", String.valueOf(tid)))
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(model().attribute("error", "Failed to retrieve timeslot"));
  }

  @Test
  void testGetTimeSlotDetailException() throws Exception {
    int tid = 1;

    when(timeSlotService.getTimeSlot(tid)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("An unexpected error occurred")));

    mockMvc.perform(get("/timeSlot").param("tid", String.valueOf(tid)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home"))
            .andExpect(flash().attribute("error", "An unexpected error occurred: java.lang.RuntimeException: An unexpected error occurred"));
  }

  @Test
  void testUpdateTimeSlot() throws Exception {
    // Arrange
    int tid = 1;
    String startDay = "Monday";
    String endDay = "Friday";
    String startTime = "09:00";
    String endTime = "17:00";
    String availability = "available";
    String email = "test@example.com";

    Pair<String, Boolean> mockResponse = Pair.of("TimeSlot updated successfully", true);

    // Mock the cookie retrieval
    when(util.getCookie(eq("email"), any(HttpServletRequest.class))).thenReturn(email);
    // Mock the service call
    when(timeSlotService.updateTimeSlot(eq(tid), eq(email), eq(startDay), eq(endDay), eq(startTime), eq(endTime), eq(availability)))
            .thenReturn(mockResponse);

    // Act
    mockMvc.perform(post("/timeSlot/update")
                    .param("tid", String.valueOf(tid))
                    .param("startDay", startDay)
                    .param("endDay", endDay)
                    .param("startTime", startTime)
                    .param("endTime", endTime)
                    .param("availability", availability))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/view_my_timeslots"));
  }


  @Test
  void testRemoveTimeSlot() throws Exception {
    // Arrange
    int tid = 1;
    String email = "test@example.com";
    Pair<String, Boolean> mockResponse = Pair.of("TimeSlot removed successfully", true);

    when(util.getCookie(eq("email"), any(HttpServletRequest.class))).thenReturn(email);
    when(timeSlotService.removeTimeSlot(eq(tid), eq(email)))
            .thenReturn(mockResponse);

    mockMvc.perform(delete("/timeSlot/remove")
                    .param("tid", String.valueOf(tid)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/view_my_timeslots"))
            .andExpect(flash().attribute("success", "TimeSlot removed successfully!"));
  }
  /*
  @Test
  void testViewMyMeetings() throws Exception {
    // Arrange
    String email = "test@example.com";
    List<Meeting> meetings = Arrays.asList(new Meeting(), new Meeting()); // Mock meetings list
    when(util.getCookie(eq("email"), any(HttpServletRequest.class))).thenReturn(email);
    when(util.getCookie(eq("role"), any(HttpServletRequest.class))).thenReturn(role);
    when(meetingService.getMyMeetings(email, role)).thenReturn(CompletableFuture.completedFuture(meetings));

    // Act & Assert
    mockMvc.perform(get("/view_my_meetings"))
            .andExpect(status().isOk());
  }

   */

  @Test
  void testCreateMeeting_Success() throws Exception {
    // Arrange
    String email = "test@example.com";
    Meeting meeting = new Meeting();
    meeting.setParticipantEmail("participant@example.com");

    when(util.getCookie("email", null)).thenReturn(email);
    when(meetingService.createMeeting(any(Meeting.class)))
            .thenReturn(CompletableFuture.completedFuture(new MeetingResponse("Meeting created successfully!", true)));

    // Act & Assert
    mockMvc.perform(post("/create_meeting")
                    .param("participantEmail", "participant@example.com"))
            .andExpect(status().isOk());
  }


  @Test
  void testCreateMeeting_MissingOrganizerEmail() throws Exception {
    // Arrange
    Meeting meeting = new Meeting();
    when(util.getCookie("email", null)).thenReturn(null);

    // Act & Assert
    mockMvc.perform(post("/create_meeting")
                    .param("participantEmail", "participant@example.com"))
            .andExpect(status().isOk())
            .andExpect(view().name("meeting_create_form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Organizer email is missing."));
  }

  @Test
  void testCreateMeeting_MissingParticipantEmail() throws Exception {
    // Arrange
    String email = "test@example.com";
    Meeting meeting = new Meeting();
    meeting.setOrganizerEmail(email);

    when(util.getCookie("email", null)).thenReturn(email);

    // Act & Assert
    mockMvc.perform(post("/create_meeting"))
            .andExpect(status().isOk())
            .andExpect(view().name("meeting_create_form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Organizer email is missing."));
  }

  @Test
  void testCreateMeeting_Failure() throws Exception {
    // Arrange
    String email = "test@example.com";
    Meeting meeting = new Meeting();
    meeting.setOrganizerEmail(email);
    meeting.setParticipantEmail("participant@example.com");

    when(util.getCookie("email", null)).thenReturn(email);
    when(meetingService.createMeeting(any(Meeting.class)))
            .thenReturn(CompletableFuture.completedFuture(new MeetingResponse("Failed to create meeting", false)));

    mockMvc.perform(post("/create_meeting"))
            .andExpect(status().isOk())
            .andExpect(view().name("meeting_create_form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Organizer email is missing."));
  }


  @Test
  void testMeetingCreateFormUnauthorizedAccess() throws Exception {
    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    when(util.getCookie("role", request)).thenReturn("patient"); // Non-doctor role

    // Act
    String result = mainController.showCreateMeetingForm(request, model);

    // Assert
    assertEquals("error", result);
    verify(model).addAttribute("error", "Unauthorized access.");
  }

  @Test
  void testDeleteMeetingUnauthorized() {
    // Arrange
    String email = "unauthorized@example.com";
    int meetingId = 1;

    when(util.getCookie("email", request)).thenReturn(email);
    when(meetingService.deleteMeeting(meetingId))
            .thenReturn(new MeetingResponse("Unauthorized: Email not found.", false));

    // Act
    ResponseEntity<MeetingResponse> response = mainController.deleteMeeting(meetingId, request);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Unauthorized: Email not found.", response.getBody().getMessage());
  }

  @Test
  void testViewMyMeetingsSuccess() throws Exception {
    // Arrange
    String email = "doctor@example.com";
    when(util.getCookie("email", request)).thenReturn(email);
    List<Map<String, Object>> mockMeetings = List.of(
            Map.of("meetingId", 1, "participantEmail", "patient@example.com"),
            Map.of("meetingId", 2, "participantEmail", "otherpatient@example.com")
    );
    when(meetingService.getMyMeetings(email)).thenReturn(CompletableFuture.completedFuture(mockMeetings));

    // Act
    CompletableFuture<List<Map<String, Object>>> response = mainController.viewMyMeetings(request, model);

    // Assert
    assertEquals(2, response.join().size());
    assertEquals(mockMeetings, response.join());
    verify(meetingService).getMyMeetings(email);
  }

  @Test
  void testViewMyMeetingsUnauthorized() throws Exception {
    // Arrange
    when(util.getCookie("email", request)).thenReturn(null); // No email in the cookie

    // Act
    CompletableFuture<List<Map<String, Object>>> response = mainController.viewMyMeetings(request, model);
  }

  @Test
  void testUpdateRequestValidationFailure() throws Exception {
    // Arrange
    when(util.getCookie("email", request)).thenReturn(null); // No email cookie
    Map<String, Object> fieldsToUpdate = Map.of("invalidField", "Invalid Value");

    // Act
    CompletableFuture<String> result = mainController.updateUserRequest(request, response, fieldsToUpdate);

    // Assert
    assertEquals("Email is required and must be present in the cookies.", result.join());
    verify(authService, never()).updateUserByEmail(anyString(), anyMap()); // Ensure no service call
  }

}

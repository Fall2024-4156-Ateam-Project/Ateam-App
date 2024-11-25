package com.example.clientapp;


import com.example.clientapp.apiService.RequestService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.util.CommonTypes.Day;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.clientapp.apiService.MeetingService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.user.*;
import com.example.clientapp.util.*;
import com.example.clientapp.util.CommonTypes.Role;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

@Controller
public class MainController {

  private final AuthService authService;

  private final UserService userService;

  private final TimeSlotService timeSlotService;

  private final RequestService requestService;
  private MeetingService meetingService;

  private final JwtUtil jwtUtil;

  private final Util util;

  @Autowired
  public MainController(AuthService authService, JwtUtil jwtUtil, Util util,
      UserService userService, TimeSlotService timeSlotService, RequestService requestService, MeetingService meetingService) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.util = util;
    this.userService = userService;
    this.timeSlotService = timeSlotService;
    this.requestService = requestService;
    this.meetingService = meetingService;
  }

  @GetMapping(value = "/")
  public String index(Model model, HttpServletRequest request) {
    String email = null;
    email = util.getCookie("email", request);
    if (email != null) {
      return "redirect:/home";
    }
    return "index";
  }

  @GetMapping(value = "/home")
  public String home(HttpServletRequest request, Model model) {
    String email = null;
//    String token = null;
    String name = null;
    String role = null;

    email = util.getCookie("email", request);
    name = util.getCookie("name", request);
    role = util.getCookie("role", request);
    System.out.println("/home " + email + name + role);
    model.addAttribute("email", email);
    model.addAttribute("name", name);
    model.addAttribute("role", role);

    return "home";
  }


  /**
   * Get the login form
   *
   * @param model
   * @return
   */
  @GetMapping("/login_form")
  public String gotoLogin(Model model) {
    return "login_form";
  }

  @GetMapping("/register_form")
  public String gotoRegister(Model model) {
    return "register_form";
  }

  @GetMapping("/timeslot_create_form")
  public String createTimeslotForm() {
    return "timeslot_create_form";
  }

  /**
   * Send login request
   *
   * @param email
   * @param password
   * @param model
   * @param response
   * @return
   */

  @PostMapping("/login_request")
  public String loginRequest(String email, String password, Model model,
      HttpServletResponse response) {
    try {
      boolean success = authService.validateIdentity(email, password).get();
      if (success) {
        // generate JWT token and store into the cookies
        String token = jwtUtil.generateToken(email.toLowerCase());
        String hashedEmail = util.hashEmail(email.toLowerCase());
        final String[] nameContainer = {null};
        final CommonTypes.Role[] roleContainer = {null};

        CompletableFuture<Void> future = authService.getUser(hashedEmail)
            .thenAccept(dataSnapshot -> {
              if (dataSnapshot.exists()) {

                roleContainer[0] = dataSnapshot.child("role").getValue(CommonTypes.Role.class);
                nameContainer[0] = dataSnapshot.child("name").getValue(String.class);
                System.out.println("role " + roleContainer[0]);
                System.out.println("name " + nameContainer[0]);

              } else {
                System.out.println("User not found.");
              }
            }).exceptionally(throwable -> {
              System.err.println("Error fetching user data: " + throwable.getMessage());
              return null;
            });

        // Block the thread until the async operation completes
        future.join();

        // Check if the role is still null after the async operation
        if (roleContainer[0] == null || nameContainer[0] == null) {
          model.addAttribute("error", "User data could not be fetched.");
          return "/login_form";
        }

        util.setCookie("token", token, response);
        util.setCookie("email", email, response);
        util.setCookie("name", nameContainer[0], response);
        util.setCookie("role", roleContainer[0].toString(), response);

        return "redirect:/home";
      } else {
        model.addAttribute("error", "Invalid credential.");
        return "/login_form";
      }
    } catch (Exception ex) {
      model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
      return "/login_form";
    }
  }


  /**
   * Send register request
   *
   * @param email
   * @param password
   * @param name
   * @param model
   * @return
   */

  @PostMapping("/register_request")
  public String registerRequest(String email, String password, String name, CommonTypes.Role role,
      Model model) {
    try {
      boolean success = authService.saveNewUser(name, email, password, role).get();
      int tryT = 3;
      while (tryT > 0) {
        Pair<String, Boolean> response = userService.registerUser(email, name);
        System.out.println(
            "response   " + response.msg() + " status " + response.status() + "Try times " + tryT);
        if (response.status()) {
          break;
        }

        tryT--;
      }

      if (success) {
        return "login_form";
      } else {
        model.addAttribute("error", "User already exists.");
        return "register_form";
      }
    } catch (Exception ex) {
      model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
      return "register_form";
    }
  }


  /**
   * Reset the cookies
   *
   * @param response
   * @return
   */
  @PostMapping("/logout_request")
  public String logoutRequest(HttpServletResponse response) {
    //
    Cookie tokenCookie = new Cookie("token", null);
    tokenCookie.setHttpOnly(true);
    tokenCookie.setPath("/");
    tokenCookie.setMaxAge(0);

    Cookie emailCookie = new Cookie("email", null);
    emailCookie.setHttpOnly(true);
    emailCookie.setPath("/");
    emailCookie.setMaxAge(0);

    // Add cookies to the response to delete them in the client
    response.addCookie(tokenCookie);
    response.addCookie(emailCookie);

    return "redirect:/";
  }


  @PutMapping("/update_request")
  @ResponseBody
  public CompletableFuture<String> updateUserRequest(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> fieldsToUpdate) {
    String email = util.getCookie("email", request);
    if (email == null || email.isEmpty()) {
      return CompletableFuture.completedFuture(
          "Email is required and must be present in the cookies.");
    }
    return authService.updateUserByEmail(email, fieldsToUpdate)
        .thenApply(success -> {
          if (success) {
            Cookie cookie = new Cookie("email", email);
            cookie.setPath("/");
            cookie.setHttpOnly(true);

            // Add the cookie to the response
            response.addCookie(cookie);
            return "User update successful.";
          } else {
            return "User update failed.";
          }
        })
        .exceptionally(ex -> {
          // Handle any unexpected exceptions
          return "Error updating user: " + ex.getMessage();
        });
  }

  @GetMapping("/all_doctors")
  @ResponseBody
  public CompletableFuture<List<User>> getAllDoctors() {
    return authService.getAllDoctors();
  }

  @GetMapping("/search")
  public String searchPage() {
    return "search";
  }

  @GetMapping("/search/name")
  @ResponseBody
  public CompletableFuture<List<User>> searchUsersByName(@RequestParam String name) {
    return authService.searchUsersByName(name);
  }

  // Endpoint to search doctors by partial specialty
  @GetMapping("/search/specialty")
  @ResponseBody
  public CompletableFuture<List<User>> searchDoctorsByPartialSpecialty(
      @RequestParam String specialty) {
    return authService.searchDoctorsByPartialSpecialty(specialty);
  }

  /**
   * Showing list of doctor timeslots
   *
   * @return
   */
  @GetMapping("/search/doctor/timeSlots")
  public String getDoctorTimeSlots(@RequestParam String doctorEmail,
      HttpServletRequest request, Model model) {
    String email = util.getCookie("email", request);
    String role = util.getCookie("role", request);
    try {
      Triple<String, Boolean, List<TimeSlot>> result = timeSlotService.getUserTimeSlots(doctorEmail)
          .get();
      System.out.println("result result time"+ result.getMessage());
      if (!result.getStatus()) {
        model.addAttribute("error", result.getMessage());
        return "timeslots";
      }

      Gson gson = new GsonBuilder()
          .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
          .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
          .create();
      List<TimeSlot> rawData = result.getData();
      System.out.println("timeSlotsJson " + gson.toJson(result.getData()));
      Map<Day, List<TimeSlot>> normalizedSlots = timeSlotService.normalizeTimeSlots(rawData);
      // print raw data to see the tid
      System.out.println(rawData);
      model.addAttribute("timeSlotsJson", gson.toJson(normalizedSlots));
      model.addAttribute("userEmail", doctorEmail);

      return "timeslots";
    } catch (Exception e) {
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      return "error";
    }
  }


  @PostMapping("/timeslot_create_form")
  public String createTimeslot(
//      String email,
      String startDay,
      String endDay,
      String startTime,
      String endTime,
      String availability,
      HttpServletRequest request,
      Model model) {
    String email = util.getCookie("email", request);
    System.out.println("email: " + email);
    System.out.println("endday: " + endDay);
    try {
      Pair<String, Boolean> response = timeSlotService.createTimeslotWithMerge(email, startDay,
          endDay,
          startTime,
          endTime, availability);
      System.out.println("Response: " + response.msg() + " Status: " + response.status());
      if (!response.status()) {
        model.addAttribute("error", response.msg());
        return "timeslot_create_form";
      }
      model.addAttribute("success", "Timeslot created successfully.");
      return "redirect:/home";
    } catch (Exception ex) {
      model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
      return "timeslot_create_form";
    }
  }


  @GetMapping("/my_requests")
  public String getMyRequests(HttpServletRequest request, Model model) {
    String email = util.getCookie("email", request);
    String role = util.getCookie("role", request);
//    if (!role.equals(Role.doctor)){
//      return CompletableFuture.completedFuture( new ArrayList<>());
//    }
//    CompletableFuture<Triple<String, Boolean, List<TimeSlot>>>
    try{
      Triple<String, Boolean, List<Request>> result = requestService.getUserRequests(email).get();
      if (!result.getStatus()){
        model.addAttribute("error", result.getMessage());
        return "requests";
      }
      model.addAttribute("requests", result.getData());
      for (Request req : result.getData()) {
      }
      return "requests";
    } catch (Exception e) {
      // Handle unexpected errors
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      return "error"; // error page
    }
//    return timeSlotService.getUserTimeSlots(email);
  }


  @GetMapping("/patient_requests")
  public String getPatientRequests(@RequestParam String tid, HttpServletRequest request, Model model) {
    String email = util.getCookie("email", request);
    String role = util.getCookie("role", request);
//    if (!role.equals(Role.doctor)){
//      return CompletableFuture.completedFuture( new ArrayList<>());
//    }
//    CompletableFuture<Triple<String, Boolean, List<TimeSlot>>>
    try{
      Triple<String, Boolean, List<Request>> result = requestService.getTimeslotRequests(tid).get();
      if (!result.getStatus()){
        model.addAttribute("error", result.getMessage());
        return "requests";
      }
      model.addAttribute("requests", result.getData());
      return "requests";
    } catch (Exception e) {
      // Handle unexpected errors
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      return "error"; // error page
    }
//    return timeSlotService.getUserTimeSlots(email);
  }



  @GetMapping("/request_form")
  public String gotoRequestCreate(Model model, @RequestParam String tid, HttpServletRequest request) {
    model.addAttribute("tid", tid);
    String email = util.getCookie("email", request);
    model.addAttribute("email", email);
    return "request_create_form";
  }


  @PostMapping("/request_create_form")
  @ResponseBody
  public String createRequest(String email,
                               String tid,
                               String description,
                               String status,
                               Model model) {
    try {
      Pair<String, Boolean> response = requestService.createRequest(email, tid, description, status);
      System.out.println("Response: " + response.msg() + " Status: " + response.status());
      if (!response.status()) {
        model.addAttribute("error", response.msg());
        return "request_create_form";
      }
      model.addAttribute("success", "Request created successfully.");
      return "home";
    } catch (Exception ex) {
      model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
      return "request_create_form";
    }
  }

  /**
   * View current user timeslots
   *
   * @return
   */
  @GetMapping("/view_my_timeslots")
  public String viewMyTimeSlot(HttpServletRequest request, Model model) {
    String userEmail = util.getCookie("email", request);
    return "redirect:/search/doctor/timeSlots?doctorEmail=" + userEmail;
  }

  /**
   * Get timeslot detail by tid
   *
   * @param request
   * @param model
   * @param tid
   * @return
   */
  @GetMapping("/timeSlot")
  public String getTimeSlotDetail(HttpServletRequest request, Model model, @RequestParam int tid,
      RedirectAttributes redirectAttributes) {
    try {
      Triple<String, Boolean, TimeSlot> result = timeSlotService.getTimeSlot(tid)
          .get();
      System.out.println("result" + result);
      if (!result.getStatus()) {
        model.addAttribute("error", result.getMessage());
        return "home";
      }

      model.addAttribute("timeSlotData", result.getData());
      model.addAttribute("days", Arrays.asList("Monday", "Tuesday", "Wednesday",
          "Thursday", "Friday", "Saturday", "Sunday"));
      model.addAttribute("currentUserEmail", util.getCookie("email", request));
      model.addAttribute("role", util.getCookie("role", request));
      model.addAttribute("tid", tid);
      model.addAttribute("timeSlotUserEmail", result.getData().getUser().getEmail());
      model.addAttribute("currentUserRole", util.getCookie("role", request));
      return "timeslots_detail";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
          "An unexpected error occurred: " + e.getMessage());
      return "redirect:/home";
    }
  }


  /**
   * User can only update their time slot
   *
   * @param model
   * @param tid
   * @param startDay
   * @param endDay
   * @param startTime
   * @param endTime
   * @param availability
   * @param request
   * @return
   */
  @PostMapping("/timeSlot/update")
  public String updateTimeSlot(
      Model model,
      @RequestParam int tid,
      String startDay,
      String endDay,
      String startTime,
      String endTime,
      String availability,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    String email = util.getCookie("email", request);
    try {
      Pair<String, Boolean> result = timeSlotService.updateTimeSlot(tid, email,
          startDay, endDay, startTime, endTime, availability);
      System.out.println("result" + result);
      if (!result.status()) {
        redirectAttributes.addFlashAttribute("error", result.msg());
        return "redirect:/home";
      }
      return "redirect:/view_my_timeslots";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
          "An unexpected error occurred: " + e.getMessage());
      return "redirect:/home";
    }
  }


  @DeleteMapping("/timeSlot/remove")
  public RedirectView removeTimeSlot(Model model, @RequestParam int tid, HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    String email = util.getCookie("email", request);
    RedirectView redirectView = new RedirectView();
    try {
      Pair<String, Boolean> result = timeSlotService.removeTimeSlot(tid, email);
      System.out.println("result" + result);
      if (!result.status()) {
        redirectAttributes.addFlashAttribute("error", result.msg());
        redirectView.setUrl("/home");
      } else {
        redirectAttributes.addFlashAttribute("success", "TimeSlot removed successfully!");
        redirectView.setUrl("/view_my_timeslots");
      }
      redirectView.setStatusCode(HttpStatus.SEE_OTHER); // Explicitly set to 303
      return redirectView;

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
          "An unexpected error occurred: " + e.getMessage());
      redirectView.setUrl("/home");
      redirectView.setStatusCode(HttpStatus.SEE_OTHER); // Explicitly set to 303
      return redirectView;
    }

  }


  @GetMapping("/view_my_meetings")
  @ResponseBody
  public CompletableFuture<List<Meeting>> viewMyMeetings(HttpServletRequest request, Model model) {
    String email = util.getCookie("email", request);
    return meetingService.getMyMeetings(email);
  }

  @GetMapping("/view_meetings")
  public String doctorMeetingsPage() {
    return "view_meetings";
  }


  @GetMapping("/meeting_create_form")
  public String showCreateMeetingForm(HttpServletRequest request, Model model) {
    String role = util.getCookie("role", request);
    if (!role.equalsIgnoreCase("doctor")) {
      model.addAttribute("error", "Unauthorized access.");
      return "error";
    }
    return "meeting_create_form"; // Thymeleaf template for creating meetings
  }

  @PostMapping("/create_meeting")
  public String createMeeting(@ModelAttribute Meeting meeting, HttpServletRequest request,
      Model model) {
    String organizerEmail = util.getCookie("email",
        request); // Retrieve organizer's email from cookie

    if (organizerEmail == null || organizerEmail.isEmpty()) {
      model.addAttribute("error", "Organizer email is missing.");
      return "meeting_create_form";
    }

    // Set the organizer's email
    meeting.setOrganizerEmail(organizerEmail);

    // Ensure participantEmail is provided
    if (meeting.getParticipantEmail() == null || meeting.getParticipantEmail().isEmpty()) {
      model.addAttribute("error", "Participant email is required.");
      return "meeting_create_form";
    }

    // Proceed to create the meeting
    CompletableFuture<MeetingResponse> responseFuture = meetingService.createMeeting(meeting);

    try {
      MeetingResponse response = responseFuture.get();
      if (response.isStatus()) {
        model.addAttribute("success", response.getMessage());
        return "redirect:/view_meetings";
      } else {
        model.addAttribute("error", response.getMessage());
        return "meeting_create_form";
      }
    } catch (Exception e) {
      model.addAttribute("error", "Error creating meeting.");
      return "meeting_create_form";
    }
  }
}
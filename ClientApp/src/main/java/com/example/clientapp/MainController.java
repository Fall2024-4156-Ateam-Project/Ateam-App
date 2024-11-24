package com.example.clientapp;



import com.example.clientapp.util.CommonTypes.Day;
import com.example.clientapp.util.LocalTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.clientapp.apiService.MeetingService;
import com.example.clientapp.apiService.TimeSlotService;
import com.example.clientapp.apiService.UserService;
import com.example.clientapp.user.*;
import com.example.clientapp.util.*;
import com.example.clientapp.util.CommonTypes.Role;
import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Triple;
import com.example.clientapp.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@Controller
public class MainController {

  private final AuthService authService;

  private final UserService userService;

  private final TimeSlotService timeSlotService;

  private MeetingService meetingService;

  private final JwtUtil jwtUtil;

  private final Util util;

  @Autowired
  public MainController(AuthService authService, JwtUtil jwtUtil, Util util,
      UserService userService, TimeSlotService timeSlotService, MeetingService meetingService) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.util = util;
    this.userService = userService;
    this.timeSlotService = timeSlotService;
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
//    if (!role.equals(Role.doctor)){
//      return CompletableFuture.completedFuture( new ArrayList<>());
//    }
//    CompletableFuture<Triple<String, Boolean, List<TimeSlot>>>
    try {
      Triple<String, Boolean, List<TimeSlot>> result = timeSlotService.getUserTimeSlots(doctorEmail)
          .get();
      if (!result.getStatus()) {
        model.addAttribute("error", result.getMessage());
        return "timeslots";
      }

      Gson gson = new GsonBuilder()
          .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
          .create();
      List<TimeSlot> rawData = timeSlotService.getUserTimeSlots(doctorEmail).get().getData();
      System.out.println("timeSlotsJson " + gson.toJson(result.getData()));
      Map<Day, List<TimeSlot>> normalizedSlots = timeSlotService.normalizeTimeSlots(rawData);
      model.addAttribute("timeSlotsJson", gson.toJson(normalizedSlots));
      model.addAttribute("userEmail", doctorEmail);
//      model.addAttribute("daysOfWeek",
//          List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
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
      Pair<String, Boolean> response = timeSlotService.createTimeslotWithMerge(email, startDay, endDay,
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



  /**
   * View current user timeslots
   *
   * @return
   */
  @GetMapping("/view_my_timeslots")
  public String viewMyTimeSlot(HttpServletRequest request, Model model) {
    String userEmail = util.getCookie("email", request);
    try {
      Triple<String, Boolean, List<TimeSlot>> result = timeSlotService.getUserTimeSlots(userEmail)
          .get();
      if (!result.getStatus()) {
        model.addAttribute("error", result.getMessage());
        return "timeslots";
      }

      Gson gson = new GsonBuilder()
          .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
          .create();
      List<TimeSlot> rawData = result.getData();
      System.out.println("timeSlotsJson " + gson.toJson(result.getData()));
      Map<Day, List<TimeSlot>> normalizedSlots = timeSlotService.normalizeTimeSlots(rawData);
      model.addAttribute("timeSlotsJson", gson.toJson(normalizedSlots));
      model.addAttribute("userEmail", userEmail);
      return "timeslots";
    } catch (Exception e) {
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      return "error";
    }
  }

  /**
   * Get timeslot detail by tid
   * @param request
   * @param model
   * @param tid
   * @return
   */
  @GetMapping("/timeSlot")
  public String getTimeSlotDetail(HttpServletRequest request, Model model, @RequestParam int tid){

    try {
      Triple<String, Boolean, TimeSlot> result = timeSlotService.getTimeSlot(tid)
          .get();
      System.out.println("result" + result);
      if (!result.getStatus()) {
        model.addAttribute("error", result.getMessage());
        return "home";
      }

      model.addAttribute("timeSlotData", result.getData());
      model.addAttribute("currentUserEmail", util.getCookie("email", request));
      return "timeslots_detail";
    } catch (Exception e) {
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      return "error";
    }
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
  public String createMeeting(@ModelAttribute Meeting meeting, HttpServletRequest request, Model model) {
    String organizerEmail = util.getCookie("email", request); // Retrieve organizer's email from cookie

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
        return "redirect:/meetings";
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
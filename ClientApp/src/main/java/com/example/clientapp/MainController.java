package com.example.clientapp;


import com.example.clientapp.apiService.UserService;
import com.example.clientapp.user.AuthService;
import com.example.clientapp.user.Doctor;
import com.example.clientapp.user.User;
import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@Controller
public class MainController {

  private final AuthService authService;

  private final UserService userService;
  private final JwtUtil jwtUtil;

  private final Util util;

  @Autowired
  public MainController(AuthService authService, JwtUtil jwtUtil, Util util,
      UserService userService) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.util = util;
    this.userService = userService;
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


}
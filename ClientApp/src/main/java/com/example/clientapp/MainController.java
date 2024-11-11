package com.example.clientapp;


import com.example.clientapp.user.AuthService;
import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {

  private final AuthService authService;


  private final JwtUtil jwtUtil;

  private final Util util;

  @Autowired
  public MainController(AuthService authService, JwtUtil jwtUtil, Util util) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.util = util;
  }

  @GetMapping(value = "/")
  public String index() {
    return "redirect:/home";
  }

  @GetMapping(value = "/home")
  public String home(HttpServletRequest request, Model model) {
    String email = null;
//    String token = null;

    email = util.getCookie("email", request);
    model.addAttribute("email", email);

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

  @PostMapping("/login_request")
  public String loginRequest(String email, String password, Model model,
      HttpServletResponse response) {
    try {
      boolean success = authService.validateIdentity(email, password).get();
      if (success) {
        // generate JWT token and store into the cookies

        //Store the token cookies & some user info
        String token = jwtUtil.generateToken(email.toLowerCase());
        Cookie tokenCookie = new Cookie("token", token);
        Cookie emailCookie = new Cookie("email", email.toLowerCase());

        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(3600 * 10); // 10 hours
        response.addCookie(tokenCookie);

        emailCookie.setHttpOnly(true);
        emailCookie.setPath("/");
        emailCookie.setMaxAge(3600 * 10); // 10 hours
        response.addCookie(emailCookie);

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

  @PostMapping("/register_request")
  public String registerRequest(String email, String password, String name, Model model) {
    try {
      boolean success = authService.saveNewUser(name, email, password).get();

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




}


package com.example.clientapp;

import com.example.clientapp.user.AuthService;
import com.example.clientapp.user.User;
import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.apiService.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainControllerTest {

  @InjectMocks
  private MainController mainController;

  @Mock
  private AuthService authService;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private Util util;

  @Mock
  private UserService userService;

  @Mock
  private Model model;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
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
}

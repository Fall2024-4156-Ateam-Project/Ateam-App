package com.example.clientapp.user;

public class User {

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  private String email;
  private String password;
  private String name;

  public User(String email, String password, String name) {
    this.email = email;
    this.password = password;
    this.name = name;
  }

  private String token;

}

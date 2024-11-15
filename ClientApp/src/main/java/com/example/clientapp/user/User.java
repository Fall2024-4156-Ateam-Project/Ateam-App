package com.example.clientapp.user;

import java.time.LocalDate;

import com.example.clientapp.util.CommonTypes;

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

  public User(String email, String password, String name, CommonTypes.Role role) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
  }

  private String token;

  private CommonTypes.Role role;
  private CommonTypes.Gender gender;
  private LocalDate dateOfBirth;

  public CommonTypes.Role getRole() {
    return this.role;
  }

  public void setRole(CommonTypes.Role role) {
    this.role = role;
  }

  public CommonTypes.Gender getGender() {
    return this.gender;
  }

  public void setGender(CommonTypes.Gender gender) {
    this.gender = gender;
  }

  public LocalDate getBirthday() {
    return this.dateOfBirth;
  }

  public void setBirthday(LocalDate birthday) {
    this.dateOfBirth = birthday;
  }


}
package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

  @Test
  void testUserConstructorAndGetters() {
    LocalDate birthday = LocalDate.of(2024, 11, 26);
    User user = new User("test@email.com", "testpassword123", "Tester", CommonTypes.Role.patient);
    user.setGender(CommonTypes.Gender.male);
    user.setBirthday(birthday);
    user.setToken("test-token");

    assertEquals("test@email.com", user.getEmail());
    assertEquals("testpassword123", user.getPassword());
    assertEquals("Tester", user.getName());
    assertEquals(CommonTypes.Role.patient, user.getRole());
    assertEquals(CommonTypes.Gender.male, user.getGender());
    assertEquals(birthday, user.getBirthday());
    assertEquals("test-token", user.getToken());
  }

  @Test
  void testSetters() {
    User user = new User();
    LocalDate birthday = LocalDate.of(2024, 11, 1);

    user.setEmail("newtest@email.com");
    user.setPassword("newpassword");
    user.setName("newTester");
    user.setRole(CommonTypes.Role.doctor);
    user.setGender(CommonTypes.Gender.female);
    user.setBirthday(birthday);
    user.setToken("new-token");

    assertEquals("newtest@email.com", user.getEmail());
    assertEquals("newpassword", user.getPassword());
    assertEquals("newTester", user.getName());
    assertEquals(CommonTypes.Role.doctor, user.getRole());
    assertEquals(CommonTypes.Gender.female, user.getGender());
    assertEquals(birthday, user.getBirthday());
    assertEquals("new-token", user.getToken());
  }

  @Test
  void testToString() {
    User user = new User("alice@email.com", "mypassword", "Alice", CommonTypes.Role.patient);
    user.setGender(CommonTypes.Gender.female);
    user.setBirthday(LocalDate.of(2024, 12, 31));
    user.setToken("token123");

    String expectedString = "User{" +
            "email='alice@email.com'" +
            ", name='Alice'" +
            ", token='token123'" +
            ", role=patient" +
            ", gender=female" +
            ", dateOfBirth=2024-12-31" +
            '}';

    assertEquals(expectedString, user.toString());
  }
}

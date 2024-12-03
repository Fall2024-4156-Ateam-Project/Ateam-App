package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

  @Test
  void testDefaultConstructor() {
    // Arrange & Act
    Request request = new Request();

    // Assert
    assertEquals(0, request.getRequesterId());
    assertEquals(0, request.getTid());
    assertNull(request.getDescription());
    assertNull(request.getStatus());
    assertNull(request.getRequesterName());
    assertNull(request.getRequesterEmail());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Request request = new Request();

    // Act
    request.setRequesterId(101);
    request.setTid(202);
    request.setDescription("Request for meeting");
    request.setStatus(CommonTypes.RequestStatus.approved);
    request.setRequesterName("John Doe");
    request.setRequesterEmail("john.doe@example.com");

    // Assert
    assertEquals(101, request.getRequesterId());
    assertEquals(202, request.getTid());
    assertEquals("Request for meeting", request.getDescription());
    assertEquals(CommonTypes.RequestStatus.approved, request.getStatus());
    assertEquals("John Doe", request.getRequesterName());
    assertEquals("john.doe@example.com", request.getRequesterEmail());
  }

  @Test
  void testPartialInitialization() {
    // Arrange & Act
    Request request = new Request();
    request.setRequesterId(123);
    request.setTid(456);

    // Assert
    assertEquals(123, request.getRequesterId());
    assertEquals(456, request.getTid());
    assertNull(request.getDescription());
    assertNull(request.getStatus());
    assertNull(request.getRequesterName());
    assertNull(request.getRequesterEmail());
  }
}

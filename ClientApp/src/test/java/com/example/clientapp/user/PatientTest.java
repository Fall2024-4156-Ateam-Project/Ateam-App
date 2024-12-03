package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

  @Test
  void testConstructorAndBasicFields() {
    // Arrange
    String email = "patient@example.com";
    String password = "securePassword";
    String name = "Jane Doe";
    CommonTypes.Role role = CommonTypes.Role.patient;

    // Act
    Patient patient = new Patient(email, password, name, role);

    // Assert
    assertEquals(email, patient.getEmail());
    assertEquals(password, patient.getPassword());
    assertEquals(name, patient.getName());
    assertEquals(role, patient.getRole());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Patient patient = new Patient();

    // Act
    patient.setMedicalHistory("Asthma");
    patient.setAllergies("Peanuts");
    patient.setBloodType("O+");
    patient.setEmergencyContact("123-456-7890");
    patient.setInsuranceDetails("HealthPlus Insurance");
    patient.setAddress("123 Main St, Springfield");

    // Assert
    assertEquals("Asthma", patient.getMedicalHistory());
    assertEquals("Peanuts", patient.getAllergies());
    assertEquals("O+", patient.getBloodType());
    assertEquals("123-456-7890", patient.getEmergencyContact());
    assertEquals("HealthPlus Insurance", patient.getInsuranceDetails());
    assertEquals("123 Main St, Springfield", patient.getAddress());
  }

  @Test
  void testDefaultConstructor() {
    // Arrange & Act
    Patient patient = new Patient();

    // Assert
    assertNull(patient.getEmail());
    assertNull(patient.getPassword());
    assertNull(patient.getName());
    assertNull(patient.getRole());
    assertNull(patient.getMedicalHistory());
    assertNull(patient.getAllergies());
    assertNull(patient.getBloodType());
    assertNull(patient.getEmergencyContact());
    assertNull(patient.getInsuranceDetails());
    assertNull(patient.getAddress());
  }
}

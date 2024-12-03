package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

  @Test
  void testConstructorAndBasicFields() {
    // Arrange
    String email = "doctor@example.com";
    String password = "securepassword";
    String name = "Dr. John Doe";
    CommonTypes.Role role = CommonTypes.Role.doctor;

    // Act
    Doctor doctor = new Doctor(email, password, name, role);

    // Assert
    assertEquals(email, doctor.getEmail());
    assertEquals(password, doctor.getPassword());
    assertEquals(name, doctor.getName());
    assertEquals(role, doctor.getRole());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Doctor doctor = new Doctor();

    String specialty = "Cardiology";
    String qualifications = "MBBS, MD";
    String medicalLicenseNumber = "ML12345";
    LocalDate licenseExpirationDate = LocalDate.of(2025, 12, 31);
    String officeLocation = "123 Medical Center";

    // Act
    doctor.setSpecialty(specialty);
    doctor.setQualifications(qualifications);
    doctor.setMedicalLicenseNumber(medicalLicenseNumber);
    doctor.setLicenseExpirationDate(licenseExpirationDate);
    doctor.setOfficeLocation(officeLocation);

    // Assert
    assertEquals(specialty, doctor.getSpecialty());
    assertEquals(qualifications, doctor.getQualifications());
    assertEquals(medicalLicenseNumber, doctor.getMedicalLicenseNumber());
    assertEquals(licenseExpirationDate, doctor.getLicenseExpirationDate());
    assertEquals(officeLocation, doctor.getOfficeLocation());
  }

  @Test
  void testDefaultConstructor() {
    // Arrange & Act
    Doctor doctor = new Doctor();

    // Assert
    assertNull(doctor.getEmail());
    assertNull(doctor.getPassword());
    assertNull(doctor.getName());
    assertNull(doctor.getRole());
    assertNull(doctor.getSpecialty());
    assertNull(doctor.getQualifications());
    assertNull(doctor.getMedicalLicenseNumber());
    assertNull(doctor.getLicenseExpirationDate());
    assertNull(doctor.getOfficeLocation());
  }

  @Test
  void testSettersAndGettersForOptionalFields() {
    // Arrange
    Doctor doctor = new Doctor();

    // Act
    doctor.setSpecialty("Dermatology");
    doctor.setQualifications("MD");
    doctor.setMedicalLicenseNumber("D12345");
    doctor.setLicenseExpirationDate(LocalDate.of(2030, 5, 20));
    doctor.setOfficeLocation("456 Skin Care Center");

    // Assert
    assertEquals("Dermatology", doctor.getSpecialty());
    assertEquals("MD", doctor.getQualifications());
    assertEquals("D12345", doctor.getMedicalLicenseNumber());
    assertEquals(LocalDate.of(2030, 5, 20), doctor.getLicenseExpirationDate());
    assertEquals("456 Skin Care Center", doctor.getOfficeLocation());
  }
}

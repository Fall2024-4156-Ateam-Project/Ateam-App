package com.example.clientapp.user;

import java.time.LocalDate;

import com.example.clientapp.util.CommonTypes;

public class Doctor extends User {
    private String specialty;
    private String qualifications;
    private String medicalLicenseNumber;
    private LocalDate licenseExpirationDate;
    private String officeLocation;
  
    // Constructor, getters, setters, etc.
    public Doctor(String email, String password, String name, CommonTypes.Role role){
        super(email, password, name, role);
    }

    // Getter for specialty
    public String getSpecialty() {
        return specialty;
    }

    // Setter for specialty
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    // Getter for qualifications
    public String getQualifications() {
        return qualifications;
    }

    // Setter for qualifications
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    // Getter for medicalLicenseNumber
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    // Setter for medicalLicenseNumber
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    // Getter for licenseExpirationDate
    public LocalDate getLicenseExpirationDate() {
        return licenseExpirationDate;
    }

    // Setter for licenseExpirationDate
    public void setLicenseExpirationDate(LocalDate licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
    }

    // Getter for officeLocation
    public String getOfficeLocation() {
        return officeLocation;
    }

    // Setter for officeLocation
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

  }
  
package com.example.clientapp.user;

import com.example.clientapp.util.CommonTypes;

public class Patient extends User {
    private String medicalHistory;
    private String allergies;
    private String bloodType;
    private String emergencyContact;
    private String insuranceDetails;
    private String address;
  
    // Constructor, getters, setters, etc.
    public Patient(String email, String password, String name, CommonTypes.Role role){
        super(email, password, name, role);
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    // Setter for medicalHistory
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    // Getter for allergies
    public String getAllergies() {
        return allergies;
    }

    // Setter for allergies
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    // Getter for bloodType
    public String getBloodType() {
        return bloodType;
    }

    // Setter for bloodType
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    // Getter for emergencyContact
    public String getEmergencyContact() {
        return emergencyContact;
    }

    // Setter for emergencyContact
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    // Getter for insuranceDetails
    public String getInsuranceDetails() {
        return insuranceDetails;
    }

    // Setter for insuranceDetails
    public void setInsuranceDetails(String insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }

    // Getter for address
    public String getAddress() {
        return address;
    }

    // Setter for address
    public void setAddress(String address) {
        this.address = address;
    }
  }

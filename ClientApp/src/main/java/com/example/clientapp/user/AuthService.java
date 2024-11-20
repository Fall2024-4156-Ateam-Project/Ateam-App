package com.example.clientapp.user;


import com.example.clientapp.apiService.UserService;
import com.example.clientapp.util.CommonTypes;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Util;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.Query;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final FirebaseApp firebaseApp;
  private final DatabaseReference databaseReference;
  private final Util util;
  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  private UserService userService;

  /**
   * Creates an instance of the Firebase Service.
   *
   * @param firebaseApp A `FirebaseApp` object representing the Firebase configuration.
   */
  @Autowired
  public AuthService(FirebaseApp firebaseApp, Util util) {
    this.util = util;
    this.firebaseApp = firebaseApp;
    this.databaseReference = FirebaseDatabase.getInstance(firebaseApp).getReference("user");
  }

  /**
   * The database interactions...
   */

  /**
   * Get the user data, async
   *
   * @param email
   * @return
   */

  public CompletableFuture<DataSnapshot> getUser(String hashedEmail) {
    CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
    databaseReference.child(hashedEmail).addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            future.complete(dataSnapshot);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            future.completeExceptionally(new Exception(databaseError.getMessage()));
          }
        }
    );
    return future;
  }

  /**
   * Check if the user exist, async
   *
   * @param hashedEmail
   * @return
   */
  public CompletableFuture<Boolean> isUserExist(String hashedEmail) {
    return getUser(hashedEmail).thenApply(DataSnapshot -> {
      if (DataSnapshot.exists()) {
        System.out.println("The user is existed");
        return true;
      } else {
        System.out.println("The user is not existed");
        return false;
      }
    }).exceptionally(throwable -> {
      System.out.println("Error checking if user exists: " + throwable.getMessage());
      return false;
    });
  }


  /**
   * Save a user
   *
   * @param name
   * @param email
   * @param password
   */
  public CompletableFuture<Void> saveUser(String name, String hashedEmail, String password,
      String email, CommonTypes.Role role) {

    // Validate role
    if (role != CommonTypes.Role.doctor && role != CommonTypes.Role.patient) {
      return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid role: " + role));
    }
    // Hash the password before saving
    String hashedPassword = passwordEncoder.encode(password);
    System.out.println("raw saveUser   " + hashedPassword);
    User user;

    if (role == CommonTypes.Role.doctor) {
        // Create a Doctor if role is doctor
        user = new Doctor(email, hashedPassword, name, role);
    } else {
        // Create a Patient if role is patient
        user = new Patient(email, hashedPassword, name, role);
    }

    CompletableFuture<Void> future = new CompletableFuture<>();

    ApiFuture<Void> apiFuture = databaseReference.child(hashedEmail).setValueAsync(user);
    apiFuture.addListener(() -> {
      try {
        apiFuture.get(); // Wait for the async save to complete
        future.complete(null); // Complete future successfully
      } catch (Exception e) {
        future.completeExceptionally(e); // Complete future with exception
      }
    }, Executors.newSingleThreadExecutor());

    return future;
  }

  /**
   * Saving a new user
   *
   * @param name
   * @param email
   * @param password
   */
  public CompletableFuture<Boolean> saveNewUser(String name, String email, String password, CommonTypes.Role role) {
    // This one should be atomic
    // call backend api and firebase to create user
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    //Save to firebase
    String emailClean = email.toLowerCase();
    String hashedEmail = util.hashEmail(emailClean);
    isUserExist(hashedEmail).thenAccept(
        isExist -> {
          if (isExist) {
            System.out.println("Cannot store the new user");
            future.complete(false);
          } else {
            saveUser(name, hashedEmail, password, emailClean, role).thenAccept(
                    aVoid -> {
                      System.out.println("Saved!");
                      future.complete(true);
                    }
                )
                .exceptionally(throwable -> {
                  System.out.println("Error saving user: " + throwable.getMessage());
                  future.complete(false);
                  return null;
                });
          }
        }
    ).exceptionally(throwable -> {
      System.out.println("Error checking user existence: " + throwable.getMessage());
      future.complete(false);
      return null;
    });




    return future;
  }


  /**
   * Saving a new user to service and auth service
   * @param name
   * @param email
   * @param password
   * @param role
   * @return
   */
  public CompletableFuture<String> registerUserBothAsync(String name, String email, String password, CommonTypes.Role role) {
    return this.saveNewUser(name, email, password, role)
        .thenCompose(success -> {
          if (!success) {
            System.out.println("registerUserBothAsync: saveNewUser not success");
            return CompletableFuture.failedFuture(new IllegalArgumentException("User already exists."));
          }
          return userService.registerUser(email, name)
              .thenApply(response -> {
                if (!response.status()) {
                  this.deleteUser(email); // Rollback user creation
                  throw new RuntimeException(response.msg());
                }
                return "login_form"; // Success
              });
        });
  }

  public CompletableFuture<Boolean> validateIdentity(String email, String password) {
    String hashedEmail = util.hashEmail(email.toLowerCase());
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    getUser(hashedEmail).thenApply(
        userData -> {
          String targetHashedPassword = userData.child("password").getValue(String.class);
          if (!userData.exists()) {
            future.complete(false);
          } else if (passwordEncoder.matches(password, targetHashedPassword)) {
            future.complete(true);
          } else {
            future.complete(false);
          }
          return future;
        }
    );
    return future;
  }

  // Method to get all doctors' information
  public CompletableFuture<List<User>> getAllDoctors() {
    List<User> doctors = new ArrayList<>();
    CompletableFuture<List<User>> future = new CompletableFuture<>();
    Query query = databaseReference.orderByChild("role").equalTo("doctor");

    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User doctor = snapshot.getValue(User.class);
            if (doctor != null) {
              System.out.println("doctor is");
              System.out.println(doctor.getName());
              doctors.add(doctor);
            }
          }
          future.complete(doctors);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          future.completeExceptionally(databaseError.toException());
        }
    });

    return future;
  }

  // Search users by name
  public CompletableFuture<List<User>> searchUsersByName(String name) {
    CompletableFuture<List<User>> future = new CompletableFuture<>();

    // Query to search doctors by name
    Query query = databaseReference.orderByChild("name");

    query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<User> users = new ArrayList<>();

            // Iterate over all users in the database
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              User user = snapshot.getValue(User.class);
              if (user != null) {
                  // Check if the user's name contains the search term (case insensitive)
                  if (user.getName().toLowerCase().contains(name.toLowerCase())) {
                      users.add(user);  // Add user to the result list if the name contains the substring
                  }
              }
          }

            future.complete(users);  // Complete the future with the result
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            future.completeExceptionally(databaseError.toException());  // Handle error
        }
    });

    return future;  // Return the CompletableFuture to handle asynchronously
}

public CompletableFuture<List<User>> searchDoctorsByPartialSpecialty(String specialtySubstring) {
  CompletableFuture<List<User>> future = new CompletableFuture<>();

  // Normalize the search term to lowercase for case-insensitive comparison
  String searchTerm = specialtySubstring.toLowerCase();

  // Create a query to find doctors whose specialty contains the search term
  databaseReference.orderByChild("specialty").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
          List<User> doctors = new ArrayList<>();

          // Iterate over all doctors in the database
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              Doctor doctor = snapshot.getValue(Doctor.class);

              // Check if the doctor's specialty contains the search term
              if (doctor != null && doctor.getSpecialty() != null && doctor.getSpecialty().toLowerCase().contains(searchTerm)) {
                  doctors.add(doctor);  // Add doctor to the result list if it matches
              }
          }

          // Complete the future with the list of matching doctors
          future.complete(doctors);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
          future.completeExceptionally(databaseError.toException());  // Handle error
      }
  });

  return future;
}


  public CompletableFuture<Void> deleteUser(String email) {
    String hashedEmail = util.hashEmail(email);
    CompletableFuture<Void> future = new CompletableFuture<>();

    ApiFuture<Void> apiFuture = databaseReference.child(hashedEmail).removeValueAsync();
    apiFuture.addListener(() -> {
      try {
        apiFuture.get();
        future.complete(null);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Executors.newSingleThreadExecutor());

    return future;
  }



public CompletableFuture<Boolean> updateUserByEmail(String email, Map<String, Object> fieldsToUpdate) {
  String hashedEmail = util.hashEmail(email.toLowerCase());  // Hash the email to retrieve the user reference

  // Return a CompletableFuture to handle async operation
  CompletableFuture<Boolean> future = new CompletableFuture<>();

  // Call the helper function to get the user data
  getUser(hashedEmail).thenAccept(dataSnapshot -> {
      if (dataSnapshot.exists()) {
          User user = dataSnapshot.getValue(User.class);  // Retrieve the user object
          CommonTypes.Role role = user != null ? user.getRole() : null;

          if (role == null) {
              System.out.println("Role not found in user data.");
              future.complete(false);
              return;
          }

          // Create a map to hold the updated fields
          Map<String, Object> updates = new HashMap<>();

          // Iterate through fieldsToUpdate and add them to the updates map
          if (fieldsToUpdate.containsKey("gender")) {
              updates.put("gender", fieldsToUpdate.get("gender"));
          }
          if (fieldsToUpdate.containsKey("name")) {
            updates.put("name", fieldsToUpdate.get("name"));
        }
          if (fieldsToUpdate.containsKey("dateOfBirth")) {
              String dateOfBirthDateStr = (String) fieldsToUpdate.get("dateOfBirth");
              LocalDate dateOfBirth = LocalDate.parse(dateOfBirthDateStr);
              updates.put("dateOfBirth", dateOfBirth.toString());  // Store as string
          }

          // Handle updates depending on the user's role
          if (role == CommonTypes.Role.doctor) {
              // Update doctor-specific fields
              if (fieldsToUpdate.containsKey("specialty")) {
                  updates.put("specialty", fieldsToUpdate.get("specialty"));
              }
              if (fieldsToUpdate.containsKey("qualifications")) {
                  updates.put("qualifications", fieldsToUpdate.get("qualifications"));
              }
              if (fieldsToUpdate.containsKey("medicalLicenseNumber")) {
                  updates.put("medicalLicenseNumber", fieldsToUpdate.get("medicalLicenseNumber"));
              }
              if (fieldsToUpdate.containsKey("licenseExpirationDate")) {
                  String licenseExpirationDateStr = (String) fieldsToUpdate.get("licenseExpirationDate");
                  LocalDate licenseExpirationDate = LocalDate.parse(licenseExpirationDateStr);
                  updates.put("licenseExpirationDate", licenseExpirationDate.toString());
              }
              if (fieldsToUpdate.containsKey("officeLocation")) {
                  updates.put("officeLocation", fieldsToUpdate.get("officeLocation"));
              }
          } else if (role == CommonTypes.Role.patient) {
              // Update patient-specific fields
              if (fieldsToUpdate.containsKey("medicalHistory")) {
                  updates.put("medicalHistory", fieldsToUpdate.get("medicalHistory"));
              }
              if (fieldsToUpdate.containsKey("allergies")) {
                  updates.put("allergies", fieldsToUpdate.get("allergies"));
              }
              if (fieldsToUpdate.containsKey("bloodType")) {
                  updates.put("bloodType", fieldsToUpdate.get("bloodType"));
              }
              if (fieldsToUpdate.containsKey("emergencyContact")) {
                  updates.put("emergencyContact", fieldsToUpdate.get("emergencyContact"));
              }
              if (fieldsToUpdate.containsKey("insuranceDetails")) {
                  updates.put("insuranceDetails", fieldsToUpdate.get("insuranceDetails"));
              }
              if (fieldsToUpdate.containsKey("address")) {
                  updates.put("address", fieldsToUpdate.get("address"));
              }
          } else {
              System.out.println("Invalid role detected: " + role);
              future.complete(false);
              return;
          }

          // Apply the updates to Firebase
          DatabaseReference usersRef = databaseReference.child(hashedEmail);
          usersRef.updateChildren(updates, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                System.out.println("Error updating user: " + databaseError.getMessage());
                future.complete(false);  // Complete the future with failure
            } else {
                System.out.println("User updated successfully");
                future.complete(true);  // Complete the future with success
            }
          });


      } else {
          System.out.println("User not found in the database.");
          future.complete(false);
      }
  }).exceptionally(ex -> {
      System.out.println("Error retrieving user data: " + ex.getMessage());
      future.complete(false);
      return null;
  });

  return future;
}




  // public CompletableFuture<Boolean> updateUserByemail(String email, Map<String, Object> fieldsToUpdate) {
        
  //       String hashedEmail = util.hashEmail(email.toLowerCase());

  //       // Return a CompletableFuture to handle async operation
  //       return CompletableFuture.supplyAsync(() -> {
  //           try {
  //               // Retrieve the current user data to get the role and other fields
  //               //get user data by hashedemail
  //               DatabaseReference usersRef = databaseReference.child(hashedEmail);
  //               if (usersRef == null) {
  //                   System.out.println("User not found.");
  //                   return false;
  //               }

  //               // Get the role from the user data
  //               //DataSnapshot user = getUser(hashedEmail).get();
  //               User user =  getUser(hashedEmail).get().getValue(User.class);

  //               CommonTypes.Role role = (CommonTypes.Role) user.getRole();

  //               if (role == null) {
  //                   System.out.println("Role not found in user data.");
  //                   return false;
  //               }

  //               // Create a map to hold the updated fields
  //               Map<String, Object> updates = new HashMap<>();
  //               if (fieldsToUpdate.containsKey("gender")) {
  //                 updates.put("gender", fieldsToUpdate.get("gender"));
  //               }
  //               if (fieldsToUpdate.containsKey("dateOfBirth")) {
  //                 // Assuming in a specific format (e.g., "yyyy-MM-dd")
  //                 String dateOfBirthDateStr = (String) fieldsToUpdate.get("dateOfBirth");
  //                 LocalDate dateOfBirth = LocalDate.parse(dateOfBirthDateStr); // Convert to LocalDate
  //                 updates.put("licenseExpirationDate", dateOfBirth.toString()); // Store as string
  //             }

  //               // Depending on the role, update only the appropriate fields
  //               if (role == CommonTypes.Role.doctor) {
  //                   // Dynamically update the fields based on input
  //                   if (fieldsToUpdate.containsKey("specialty")) {
  //                       updates.put("specialty", fieldsToUpdate.get("specialty"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("qualifications")) {
  //                       updates.put("qualifications", fieldsToUpdate.get("qualifications"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("medicalLicenseNumber")) {
  //                       updates.put("medicalLicenseNumber", fieldsToUpdate.get("medicalLicenseNumber"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("licenseExpirationDate")) {
  //                       // Assuming "licenseExpirationDate" is a string in a specific format (e.g., "yyyy-MM-dd")
  //                       String licenseExpirationDateStr = (String) fieldsToUpdate.get("licenseExpirationDate");
  //                       LocalDate licenseExpirationDate = LocalDate.parse(licenseExpirationDateStr); // Convert to LocalDate
  //                       updates.put("licenseExpirationDate", licenseExpirationDate.toString()); // Store as string
  //                   }
  //                   if (fieldsToUpdate.containsKey("officeLocation")) {
  //                       updates.put("officeLocation", fieldsToUpdate.get("officeLocation"));
  //                   }

  //                   usersRef.updateChildrenAsync(updates);

  //               } else if (role == CommonTypes.Role.patient) {

  //                   // Dynamically update the fields based on input
  //                   if (fieldsToUpdate.containsKey("medicalHistory")) {
  //                       updates.put("medicalHistory", fieldsToUpdate.get("medicalHistory"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("allergies")) {
  //                       updates.put("allergies", fieldsToUpdate.get("allergies"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("bloodType")) {
  //                       updates.put("bloodType", fieldsToUpdate.get("bloodType"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("emergencyContact")) {
  //                       updates.put("emergencyContact", fieldsToUpdate.get("emergencyContact"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("insuranceDetails")) {
  //                     updates.put("insuranceDetails", fieldsToUpdate.get("insuranceDetails"));
  //                   }
  //                   if (fieldsToUpdate.containsKey("address")) {
  //                     updates.put("address", fieldsToUpdate.get("address"));
  //                   }

  //                   usersRef.updateChildrenAsync(updates);
                   
  //               } else {
  //                   System.out.println("Invalid role detected: " + role);
  //                   return false;
  //               }

  //               System.out.println("User updated successfully");
  //               return true;

  //           } catch (Exception e) {
  //               e.printStackTrace();
  //               return false;
  //           }
  //       });
  //   }


}

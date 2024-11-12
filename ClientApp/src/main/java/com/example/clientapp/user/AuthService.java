package com.example.clientapp.user;


import com.example.clientapp.apiService.UserService;
import com.example.clientapp.util.Pair;
import com.example.clientapp.util.Util;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
      String email) {
    // Hash the password before saving
    String hashedPassword = passwordEncoder.encode(password);
    System.out.println("raw saveUser   " + hashedPassword);
    User user = new User(email, hashedPassword, name);

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
  public CompletableFuture<Boolean> saveNewUser(String name, String email, String password) {
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
            saveUser(name, hashedEmail, password, emailClean).thenAccept(
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


}

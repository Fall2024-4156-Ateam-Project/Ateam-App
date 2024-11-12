package com.example.clientapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


@Configuration
public class FirebaseConfig {

  @Value("${app.db_url}")
  private String db_url;

  @Value("${app.serviceAccountKey_path}")
  private String path;

  @Bean
  public FirebaseApp initFirebase() throws IOException {
    ClassPathResource serviceAccount =
        new ClassPathResource(
            path);

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
        .setDatabaseUrl(db_url)
        .build();

    return FirebaseApp.initializeApp(options);

  }
}

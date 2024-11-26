package com.example.clientapp.util;

public record Pair<T, U>(T msg, U status) {
  public static <T, U> Pair<T, U> of(T msg, U status) {
    return new Pair<>(msg, status);
  }
}

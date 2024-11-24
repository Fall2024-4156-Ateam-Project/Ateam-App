package com.example.clientapp.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

  @Override
  public void write(JsonWriter out, LocalDate value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.format(formatter));
    }
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    if (in.peek() == null) {
      in.nextNull();
      return null;
    } else {
      return LocalDate.parse(in.nextString(), formatter);
    }
  }
}
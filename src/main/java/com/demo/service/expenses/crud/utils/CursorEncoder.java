package com.demo.service.expenses.crud.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CursorEncoder {

  private static final String SEPARATOR = "|";

  public static String encode(Instant date, ObjectId id) {
    String raw = date.toString() + SEPARATOR + id.toHexString();
    return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static Cursor decode(String cursor) {
    String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
    String[] parts = decoded.split("\\|");

    Instant date = Instant.parse(parts[0]);
    ObjectId id = new ObjectId(parts[1]);
    return new Cursor(date, id);
  }

  public record Cursor(Instant date, ObjectId id) {
  }
}
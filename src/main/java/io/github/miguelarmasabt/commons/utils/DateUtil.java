package io.github.miguelarmasabt.commons.utils;

import io.github.miguelarmasabt.commons.constants.Regex;
import io.github.miguelarmasabt.commons.exceptions.InvalidDateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

  public static final ZoneId LIMA_ZONE_ID = ZoneId.of("America/Lima");

  public static final DateTimeFormatter SPANISH_PATTERN = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es"));
  public static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  public static final DateTimeFormatter GMAIL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  /**
   * @return text in the format "15 dic 2026".
   */
  public static String toSpanishTextDate(Instant instant) {
    return instant
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(SPANISH_PATTERN)
        .toLowerCase();
  }

  /**
   * @param date accepts "dd-MM-yyyy" or ISO-8601.
   */
  public static Instant toInstantAtTime(String date) {
    Instant parsedInstant = parseIsoInstant(date);
    validateNotFuture(parsedInstant, date);
    return parsedInstant;
  }

  /**
   * @return text in the format "yyyy/MM/dd".
   */
  public static String toGmailDate(Instant instant) {
    return instant
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(GMAIL_DATE_FORMATTER);
  }

  /**
   * @param date is in "yyyy/MM/dd" format.
   */
  public static void validateGmailDateYyyyMmDd(String date) {
    Optional.ofNullable(date)
        .filter(d -> Pattern.matches(Regex.GMAIL_DATE_YYYY_MM_DD, d))
        .map(d -> LocalDate.parse(d, GMAIL_DATE_FORMATTER))
        .filter(d -> !d.isAfter(LocalDate.now(ZoneOffset.UTC)))
        .orElseThrow(() -> new InvalidDateException(date));
  }

  public static Instant firstDayOfCurrentMonth() {
    return LocalDate.now(DateUtil.LIMA_ZONE_ID)
        .withDayOfMonth(1)
        .atStartOfDay(DateUtil.LIMA_ZONE_ID)
        .toInstant();
  }

  public static Instant firstDayOfNextMonth() {
    return LocalDate.now(DateUtil.LIMA_ZONE_ID)
        .plusMonths(1)
        .withDayOfMonth(1)
        .atStartOfDay(DateUtil.LIMA_ZONE_ID)
        .toInstant();
  }

  private static Instant parseIsoInstant(String date) {
    try {
      return Instant.parse(normalize(date));
    } catch (Exception exception) {
      throw new InvalidDateException(date);
    }
  }

  private static void validateNotFuture(Instant instant, String originalValue) {
    if (instant.isAfter(Instant.now())) {
      throw new InvalidDateException(originalValue);
    }
  }

  private static String normalize(String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .filter(v -> !v.isBlank())
        .orElseThrow(() -> new InvalidDateException(value));
  }
}
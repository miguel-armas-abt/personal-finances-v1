package com.demo.service.finances.expenses.extracted.strategy.ibk;

import com.demo.service.finances.expenses.extracted.strategy.ExtractExpenseStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class IBKPlinStrategy implements ExtractExpenseStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  private static final Map<Long, String> MONTHS_ES = Map.ofEntries(
      Map.entry(1L, "Ene"),
      Map.entry(2L, "Feb"),
      Map.entry(3L, "Mar"),
      Map.entry(4L, "Abr"),
      Map.entry(5L, "May"),
      Map.entry(6L, "Jun"),
      Map.entry(7L, "Jul"),
      Map.entry(8L, "Ago"),
      Map.entry(9L, "Sep"),
      Map.entry(10L, "Oct"),
      Map.entry(11L, "Nov"),
      Map.entry(12L, "Dic"));

  private static final DateTimeFormatter DATE_FORMATTER =
      new DateTimeFormatterBuilder()
          .parseCaseInsensitive()
          .appendValue(ChronoField.DAY_OF_MONTH)
          .appendLiteral(' ')
          .appendText(ChronoField.MONTH_OF_YEAR, MONTHS_ES)
          .appendLiteral(' ')
          .appendValue(ChronoField.YEAR, 4)
          .appendLiteral(' ')
          .appendPattern("hh:mm a")
          .toFormatter(Locale.ENGLISH);

  @Override
  public String supports() {
    return "IBK_PLIN";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Destinatario\\s*:?\\s*(.+?)(?=\\s+Destino)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "Moneda\\s*y\\s*monto\\s*:?\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "(\\d{1,2}\\s+[A-Za-z]{3}\\s+\\d{4}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DATE_FORMATTER;
  }

}
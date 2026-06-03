package io.github.miguelarmasabt.expenses.refresh.strategy.bcp;

import io.github.miguelarmasabt.expenses.refresh.strategy.BankReceiptExpenseExtractorStrategy;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class BCPDebitCardExtractorStrategy implements BankReceiptExpenseExtractorStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  private static final Map<Long, String> MONTHS_ES = Map.ofEntries(
      Map.entry(1L, "enero"),
      Map.entry(2L, "febrero"),
      Map.entry(3L, "marzo"),
      Map.entry(4L, "abril"),
      Map.entry(5L, "mayo"),
      Map.entry(6L, "junio"),
      Map.entry(7L, "julio"),
      Map.entry(8L, "agosto"),
      Map.entry(9L, "septiembre"),
      Map.entry(10L, "octubre"),
      Map.entry(11L, "noviembre"),
      Map.entry(12L, "diciembre")
  );

  private static final Map<Long, String> AM_PM = Map.of(
      0L, "AM",
      1L, "PM"
  );

  private static final DateTimeFormatter DATE_FORMATTER =
      new DateTimeFormatterBuilder()
          .parseCaseInsensitive()
          .appendValue(ChronoField.DAY_OF_MONTH)
          .appendLiteral(" de ")
          .appendText(ChronoField.MONTH_OF_YEAR, MONTHS_ES)
          .appendLiteral(" de ")
          .appendValue(ChronoField.YEAR, 4)
          .appendLiteral(" - ")
          .appendPattern("hh:mm ")
          .appendText(ChronoField.AMPM_OF_DAY, AM_PM)
          .toFormatter(Locale.ENGLISH);

  @Override
  public String supports() {
    return "BCP_DEBIT_CARD";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Empresa\\s+(.+?)(?=\\s+N[úu]mero\\s+de\\s+operaci[oó]n)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "(?:Realizaste\\s+un\\s+consumo\\s+de|Total\\s+del\\s+consumo)\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{1,2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "Fecha\\s+y\\s+hora\\s*(\\d{1,2}\\s+de\\s+\\p{L}+\\s+de\\s+\\d{4}\\s+-\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DATE_FORMATTER;
  }
}
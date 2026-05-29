package io.github.miguelarmasabt.expenses.extracted.strategy.bbva;

import io.github.miguelarmasabt.expenses.extracted.strategy.ExtractExpenseStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class BBVAPlinStrategy implements ExtractExpenseStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  @Override
  public String supports() {
    return "BBVA_PLIN";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Plineaste\\s*(?:S/|\\$)\\s*[0-9.,]+\\s*a\\s+(.+?)(?=\\s+Detalles)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "Plineaste\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{1,2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "Fecha y hora:\\s*(\\d+ de \\w+, \\d{4} \\d{2}:\\d{2})";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy HH:mm", new Locale("es"));
  }
}
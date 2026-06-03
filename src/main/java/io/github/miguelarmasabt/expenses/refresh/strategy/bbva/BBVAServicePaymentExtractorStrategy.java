package io.github.miguelarmasabt.expenses.refresh.strategy.bbva;

import io.github.miguelarmasabt.expenses.refresh.strategy.BankReceiptExpenseExtractorStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class BBVAServicePaymentExtractorStrategy implements BankReceiptExpenseExtractorStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  @Override
  public String supports() {
    return "BBVA_SERVICE_PAYMENT";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Nombre\\s+(?:de\\s+)?servicio\\s+(.+?)(?=\\s+Descripción)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "Fecha y hora de la operación\\s*(\\d{2}\\s+\\w+,\\s+\\d{4}\\s+\\d{2}:\\d{2})";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", new Locale("es"));
  }
}
package com.demo.service.finances.expenses.extracted.strategy.bbva;

import com.demo.service.commons.utils.DateUtil;
import com.demo.service.finances.expenses.extracted.strategy.ExtractExpenseStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class BBVAPlinMerchantQRStrategy implements ExtractExpenseStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  @Override
  public String supports() {
    return "BBVA_PLIN_MERCHANT_QR";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Comercio\\s+(.+?)(?=\\s+Forma de pago)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "Fecha(?:\\s+de\\s+la\\s+operación|\\s+y\\s+hora)?\\s*(\\d{1,2} de \\p{L}+, \\d{4}(?: \\d{2}:\\d{2})?)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", new Locale("es"));
  }

  @Override
  public Instant extractDate(String text) {
    Matcher matcher = getDatePattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The field 'date' doesn't match | pattern=%s | text=%s", supports(), getDatePattern().pattern(), text);
      return Instant.now();
    }

    String rawDate = matcher.group(1);
    LocalDate date = LocalDate.parse(rawDate, getDateFormatter());
    return date.atStartOfDay(DateUtil.LIMA_ZONE_ID).toInstant();
  }
}
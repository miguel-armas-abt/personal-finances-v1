package com.demo.service.finances.expenses.extracted.strategy.bbva;

import com.demo.commons.constants.Symbol;
import com.demo.service.commons.enums.Currency;
import com.demo.service.commons.utils.DateUtil;
import com.demo.service.finances.expenses.extracted.strategy.ExtractExpenseStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class BBVADebitCardStrategy implements ExtractExpenseStrategy {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  @Override
  public String supports() {
    return "BBVA_DEBIT_CARD";
  }

  @Override
  public Pattern getRecipientPattern() {
    String regex = "Comercio\\s*:?\\s*(.+?)(?=\\s+(?:Monto|Fecha|Forma|Este))";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getAmountAndCurrencyPattern() {
    String regex = "Monto\\s*:?\\s*([0-9]+(?:[.,][0-9]{2})?)\\s*Moneda\\s*:?\\s*(PEN|USD|S/|US\\$)";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public Pattern getDatePattern() {
    String regex = "Fecha\\s*:?\\s*(\\d{2}/\\d{2}/\\d{4})\\s*Hora\\s*:?\\s*(\\d{2}:\\d{2}:\\d{2})";
    return Pattern.compile(regex, REGEX_FLAGS);
  }

  @Override
  public DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  }

  @Override
  public AmountAndCurrency extractAmountAndCurrency(String text) {
    Matcher matcher = getAmountAndCurrencyPattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The fields 'amount' and 'currency' don't match | pattern=%s | text=%s", supports(), getDatePattern().pattern(), text);
      return new AmountAndCurrency(BigDecimal.ZERO, Currency.UNKNOWN.name());
    }

    String amountString = matcher.group(1).replace(Symbol.COMMA, Symbol.DOT);
    BigDecimal amount = new BigDecimal(amountString);
    String currencyCode = matcher.group(2);
    String currency = Currency.parseFromCode(currencyCode).name();
    return new AmountAndCurrency(amount, currency);
  }

  @Override
  public Instant extractDate(String text) {
    Matcher matcher = getDatePattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The field 'date' doesn't match | pattern=%s | text=%s", supports(), getDatePattern().pattern(), text);
      return Instant.now();
    }

    String datePart = matcher.group(1);
    String timePart = matcher.group(2);
    String rawDateTime = datePart + Symbol.SPACE + timePart;

    LocalDateTime dateTime = LocalDateTime.parse(rawDateTime, getDateFormatter());
    return dateTime
        .atZone(DateUtil.LIMA_ZONE_ID)
        .toInstant();
  }
}
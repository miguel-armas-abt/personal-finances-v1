package io.github.miguelarmasabt.expenses.extracted.strategy;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.extracted.utils.ExtractExpenseUtil;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface BankReceiptExpenseExtractorStrategy {

  String UNKNOWN = "unknown";
  Logger log = Logger.getLogger(BankReceiptExpenseExtractorStrategy.class);

  String supports();

  Pattern getRecipientPattern();

  Pattern getAmountAndCurrencyPattern();

  Pattern getDatePattern();

  DateTimeFormatter getDateFormatter();

  default Uni<ExtractExpenseResponseDto> toDto(MessageContentResponseWrapper messageContent) {
    String gmailMessageId = messageContent.getId();
    String htmlBody = ExtractExpenseUtil.extractHtmlBody(messageContent.getPayload());
    String text = ExtractExpenseUtil.toPlainText(htmlBody);

    Instant date = extractDate(text);
    AmountAndCurrency amountAndCurrency = extractAmountAndCurrency(text);
    String merchantName = extractRecipient(text);

    return Uni.createFrom().item(
        ExtractExpenseResponseDto.builder()
            .gmailMessageId(gmailMessageId)
            .date(date)
            .currency(amountAndCurrency.currency())
            .amount(amountAndCurrency.amount())
            .detail(
                ExtractExpenseResponseDto.ExpenseDetail.builder()
                    .recipient(merchantName)
                    .source(supports())
                    .build()
            )
            .build()
    );
  }

  default AmountAndCurrency extractAmountAndCurrency(String text) {
    Matcher matcher = getAmountAndCurrencyPattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The fields 'amount' and 'currency' don't match | pattern=%s | text=%s",
          supports(), getAmountAndCurrencyPattern().pattern(), text);
      return new AmountAndCurrency(BigDecimal.ZERO, Currency.UNKNOWN.name());
    }

    String currencySymbol = matcher.group(1);
    String amountString = matcher.group(2).replace(Strings.COMMA, Strings.DOT);
    BigDecimal amount = new BigDecimal(amountString);
    String currency = Currency.parseFromSymbol(currencySymbol).name();
    return new AmountAndCurrency(amount, currency);
  }

  default String extractRecipient(String text) {
    Matcher matcher = getRecipientPattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The field 'recipient' doesn't match | pattern=%s | text=%s",
          supports(), getRecipientPattern().pattern(), text);
      return UNKNOWN;
    }

    return matcher.group(1).trim();
  }

  default Instant extractDate(String text) {
    Matcher matcher = getDatePattern().matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | The field 'date' doesn't match | pattern=%s | text=%s",
          supports(), getDatePattern().pattern(), text);
      return Instant.now();
    }

    String rawDate = matcher.group(1);
    LocalDateTime dateTime = LocalDateTime.parse(rawDate, getDateFormatter());
    return dateTime.atZone(DateUtil.LIMA_ZONE_ID).toInstant();
  }

  record AmountAndCurrency(BigDecimal amount, String currency) {
  }
}
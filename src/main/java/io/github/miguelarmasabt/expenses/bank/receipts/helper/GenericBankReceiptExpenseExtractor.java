package io.github.miguelarmasabt.expenses.bank.receipts.helper;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.github.miguelarmasabt.expenses.bank.receipts.utils.ExtractExpenseUtil;
import io.github.miguelarmasabt.expenses.sync.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class GenericBankReceiptExpenseExtractor {

  private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
  private static final int DEFAULT_GROUP = 1;

  private static final String UNKNOWN = "unknown";
  private static final String LOCAL_DATE_TIME = "LOCAL_DATE_TIME";
  private static final String LOCAL_DATE = "LOCAL_DATE";
  private static final String SPLIT_DATE_TIME = "SPLIT_DATE_TIME";

  private static final Logger log = Logger.getLogger(GenericBankReceiptExpenseExtractor.class);

  public Uni<ExtractExpenseResponseDto> toDto(MessageContentResponseWrapper messageContent,
                                              BankReceiptTemplateEntity template) {
    String htmlBody = ExtractExpenseUtil.extractHtmlBody(messageContent.getPayload());
    String text = ExtractExpenseUtil.toPlainText(htmlBody);

    Instant date = extractDate(text, template);
    AmountAndCurrency amountAndCurrency = extractAmountAndCurrency(text, template);
    String recipient = extractRecipient(text, template);

    return Uni.createFrom().item(
        ExtractExpenseResponseDto.builder()
            .gmailMessageId(messageContent.getId())
            .gmailMessageReceivedAt(toGmailInternalDate(messageContent))
            .date(date)
            .currency(amountAndCurrency.currency())
            .amount(amountAndCurrency.amount())
            .detail(
                ExtractExpenseResponseDto.ExpenseDetail.builder()
                    .recipient(recipient)
                    .source(template.getCode())
                    .build()
            )
            .build()
    );
  }

  private Instant toGmailInternalDate(MessageContentResponseWrapper messageContent) {
    return Instant.ofEpochMilli(Long.parseLong(messageContent.getInternalDate()));
  }

  private String extractRecipient(String text, BankReceiptTemplateEntity template) {
    BankReceiptTemplateEntity.RegexField config = template.getExtraction().getRecipient();

    return extractGroup(text, config.getRegex(), resolveGroup(config.getGroup()))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .orElseGet(() -> Optional.ofNullable(config.getDefaultValue()).orElse(UNKNOWN));
  }

  private AmountAndCurrency extractAmountAndCurrency(String text, BankReceiptTemplateEntity template) {
    BankReceiptTemplateEntity.AmountCurrency config = template.getExtraction().getAmountCurrency();
    Matcher matcher = Pattern.compile(config.getRegex(), REGEX_FLAGS).matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | amount/currency did not match | regex=%s | text=%s",
          template.getCode(), config.getRegex(), text);
      return new AmountAndCurrency(BigDecimal.ZERO, Currency.UNKNOWN.name());
    }

    String rawAmount = matcher.group(config.getAmountGroup()).replace(Strings.COMMA, Strings.DOT);
    String rawCurrency = matcher.group(config.getCurrencyGroup());

    BigDecimal amount = new BigDecimal(rawAmount);
    String currency = resolveCurrency(rawCurrency);

    return new AmountAndCurrency(amount, currency);
  }

  private Instant extractDate(String text, BankReceiptTemplateEntity template) {
    BankReceiptTemplateEntity.DateField config = template.getExtraction().getDate();
    Matcher matcher = Pattern.compile(config.getRegex(), REGEX_FLAGS).matcher(text);

    if (!matcher.find()) {
      log.errorf("%s | date did not match | regex=%s | text=%s",
          template.getCode(), config.getRegex(), text);
      return Instant.now();
    }

    String rawDate = buildRawDate(config, matcher);
    String normalizedDate = applyReplacements(rawDate, config.getReplacements());

    try {
      return parseDate(normalizedDate, config);
    } catch (RuntimeException ex) {
      log.errorf(ex, "%s | date parse failed | rawDate=%s | normalizedDate=%s | pattern=%s",
          template.getCode(), rawDate, normalizedDate, config.getPattern());
      return Instant.now();
    }
  }

  private String buildRawDate(BankReceiptTemplateEntity.DateField config, Matcher matcher) {
    if (SPLIT_DATE_TIME.equalsIgnoreCase(config.getMode())) {
      String datePart = matcher.group(config.getDateGroup());
      String timePart = matcher.group(config.getTimeGroup());
      String separator = Optional.ofNullable(config.getDateTimeSeparator()).orElse(Strings.SPACE);
      return datePart + separator + timePart;
    }

    return matcher.group(resolveGroup(config.getGroup()));
  }

  private Instant parseDate(String rawDate, BankReceiptTemplateEntity.DateField config) {
    ZoneId zoneId = Optional.ofNullable(config.getZoneId())
        .map(ZoneId::of)
        .orElse(DateUtil.LIMA_ZONE_ID);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        config.getPattern(),
        Locale.forLanguageTag(Optional.ofNullable(config.getLocale()).orElse("es-PE"))
    );

    if (LOCAL_DATE.equalsIgnoreCase(config.getMode())) {
      LocalDate localDate = LocalDate.parse(rawDate, formatter);
      return localDate.atStartOfDay(zoneId).toInstant();
    }

    if (LOCAL_DATE_TIME.equalsIgnoreCase(config.getMode())
        || SPLIT_DATE_TIME.equalsIgnoreCase(config.getMode())) {
      LocalDateTime localDateTime = LocalDateTime.parse(rawDate, formatter);
      return localDateTime.atZone(zoneId).toInstant();
    }

    throw new IllegalArgumentException("Unsupported date extraction mode: " + config.getMode());
  }

  private Optional<String> extractGroup(String text, String regex, int group) {
    Matcher matcher = Pattern.compile(regex, REGEX_FLAGS).matcher(text);
    return matcher.find()
        ? Optional.ofNullable(matcher.group(group))
        : Optional.empty();
  }

  private int resolveGroup(Integer group) {
    return Optional.ofNullable(group).orElse(DEFAULT_GROUP);
  }

  private String applyReplacements(String value, Map<String, String> replacements) {
    String result = value;

    for (Map.Entry<String, String> entry : Optional.ofNullable(replacements).orElse(Map.of()).entrySet()) {
      result = Pattern.compile(Pattern.quote(entry.getKey()), REGEX_FLAGS)
          .matcher(result)
          .replaceAll(entry.getValue());
    }

    return result;
  }

  private String resolveCurrency(String rawCurrency) {
    if (Objects.isNull(rawCurrency) || rawCurrency.isBlank()) {
      return Currency.UNKNOWN.name();
    }

    try {
      return Currency.parseFromSymbol(rawCurrency).name();
    } catch (RuntimeException ignored) {
      return resolveCurrencyFromCode(rawCurrency);
    }
  }

  private String resolveCurrencyFromCode(String rawCurrency) {
    try {
      return Currency.parseFromCode(rawCurrency).name();
    } catch (RuntimeException ignored) {
      return Currency.UNKNOWN.name();
    }
  }

  private record AmountAndCurrency(BigDecimal amount, String currency) {
  }
}
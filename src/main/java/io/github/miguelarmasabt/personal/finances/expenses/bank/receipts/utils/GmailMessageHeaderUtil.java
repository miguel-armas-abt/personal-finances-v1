package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.utils;

import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions.NoSuchHeaderFromException;
import io.github.miguelarmasabt.commons.repository.google.model.MessageHeader;
import io.github.miguelarmasabt.commons.repository.google.model.MessagePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GmailMessageHeaderUtil {

  private static final String HEADER_FROM = "From";
  private static final String HEADER_SUBJECT = "Subject";
  private static final Pattern FROM_PATTERN = Pattern.compile("<([^>]+)>");

  public static GmailMessageHeaders extractHeaders(MessagePayload payload) {
    GmailMessageHeaders headers = Optional.ofNullable(payload)
        .map(MessagePayload::getHeaders)
        .orElseGet(List::of)
        .stream()
        .filter(Objects::nonNull)
        .filter(header -> StringUtils.isNotBlank(header.getName()))
        .reduce(
            new GmailMessageHeaders(null, StringUtils.EMPTY),
            GmailMessageHeaderUtil::accumulateHeader,
            GmailMessageHeaderUtil::combineHeaders
        );

    String safeFrom = Optional.ofNullable(headers.from())
        .filter(StringUtils::isNotBlank)
        .orElseThrow(NoSuchHeaderFromException::new);

    return new GmailMessageHeaders(safeFrom, headers.subject());
  }

  private static GmailMessageHeaders accumulateHeader(GmailMessageHeaders current,
                                                      MessageHeader header) {
    if (HEADER_FROM.equalsIgnoreCase(header.getName())) {
      return new GmailMessageHeaders(extractEmailAddress(header.getValue()), current.subject());
    }

    if (HEADER_SUBJECT.equalsIgnoreCase(header.getName())) {
      return new GmailMessageHeaders(current.from(), Optional.ofNullable(header.getValue()).orElse(StringUtils.EMPTY));
    }

    return current;
  }

  private static GmailMessageHeaders combineHeaders(GmailMessageHeaders left,
                                                    GmailMessageHeaders right) {
    String from = Optional.ofNullable(left.from())
        .filter(StringUtils::isNotBlank)
        .orElse(right.from());

    String subject = StringUtils.isNotBlank(left.subject())
        ? left.subject()
        : right.subject();

    return new GmailMessageHeaders(from, subject);
  }

  private static String extractEmailAddress(String fromHeader) {
    return Optional.ofNullable(fromHeader)
        .filter(StringUtils::isNotBlank)
        .map(String::trim)
        .map(value -> {
          Matcher matcher = FROM_PATTERN.matcher(value);
          return matcher.find() ? matcher.group(1).trim() : value;
        })
        .orElse(null);
  }

  public record GmailMessageHeaders(String from, String subject) {
  }
}
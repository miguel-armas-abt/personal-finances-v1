package io.github.miguelarmasabt.expenses.extracted.strategy;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExtractExpenseHelper {

  private final Map<String, List<BankReceiptRule>> receiptMap;

  public ExtractExpenseHelper(ApplicationProperties properties) {
    this.receiptMap = properties.features().bankReceipts()
        .entrySet().stream()
        .map(entry -> BankReceiptRule.builder()
            .label(normalize(entry.getKey()))
            .from(normalize(entry.getValue().from()))
            .subject(normalize(entry.getValue().subject()))
            .build())
        .collect(Collectors.groupingBy(
            BankReceiptRule::from,
            Collectors.toUnmodifiableList()));
  }

  public Optional<String> resolveLabel(String from, String subject) {
    String normalizedFrom = normalize(from);
    String normalizedSubject = normalize(subject);

    return Optional.ofNullable(receiptMap.get(normalizedFrom))
        .stream()
        .flatMap(List::stream)
        .filter(rule -> normalizedSubject.contains(rule.subject()))
        .map(BankReceiptRule::label)
        .map(String::toUpperCase)
        .findFirst();
  }

  private String normalize(String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .map(text -> text.toLowerCase(Locale.ROOT))
        .orElse(StringUtils.EMPTY);
  }

  @Builder
  private record BankReceiptRule(String label, String from, String subject) {
  }

}

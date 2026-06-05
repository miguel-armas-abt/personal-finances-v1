package io.github.miguelarmasabt.expenses.bank.receipts.helper;

import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.github.miguelarmasabt.expenses.bank.receipts.utils.BankReceiptTextNormalizer;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class ExpenseExtractorHelper {

  public Optional<BankReceiptTemplateEntity> resolveTemplate(List<BankReceiptTemplateEntity> templates,
                                                             String from,
                                                             String subject) {
    String normalizedFrom = BankReceiptTextNormalizer.normalize(from);
    String normalizedSubject = BankReceiptTextNormalizer.normalize(subject);

    return Optional.ofNullable(templates)
        .stream()
        .flatMap(List::stream)
        .filter(Objects::nonNull)
        .filter(BankReceiptTemplateEntity::getEnabled)
        .filter(template -> matchesFrom(template, normalizedFrom))
        .filter(template -> matchesSubject(template, normalizedSubject))
        .findFirst();
  }

  private boolean matchesFrom(BankReceiptTemplateEntity template, String normalizedFrom) {
    return Optional.ofNullable(template.getGmail())
        .map(BankReceiptTemplateEntity.Gmail::getFrom)
        .map(BankReceiptTextNormalizer::normalize)
        .filter(templateFrom -> templateFrom.equals(normalizedFrom))
        .isPresent();
  }

  private boolean matchesSubject(BankReceiptTemplateEntity template, String normalizedSubject) {
    return Optional.ofNullable(template.getGmail())
        .map(BankReceiptTemplateEntity.Gmail::getSubjects)
        .orElse(List.of())
        .stream()
        .map(BankReceiptTextNormalizer::normalize)
        .anyMatch(normalizedSubject::contains);
  }
}
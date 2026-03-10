package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.helper;

import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class GmailQueryBuilder {

  public String buildBankReceiptQueryAfter(List<BankReceiptTemplateEntity> templates,
                                           String checkpointDate) {
    DateUtil.validateGmailDateYyyyMmDd(checkpointDate);

    String conditions = Optional.ofNullable(templates)
        .stream()
        .flatMap(List::stream)
        .filter(Objects::nonNull)
        .filter(BankReceiptTemplateEntity::getEnabled)
        .filter(template -> Objects.nonNull(template.getGmail()))
        .flatMap(template -> buildConditions(template).stream())
        .collect(Collectors.joining(" OR "));

    if (conditions.isBlank()) {
      return "after:" + checkpointDate;
    }

    return "(" + conditions + ") after:" + checkpointDate;
  }

  private List<String> buildConditions(BankReceiptTemplateEntity template) {
    String from = template.getGmail().getFrom();

    return Optional.ofNullable(template.getGmail().getSubjects())
        .orElse(List.of())
        .stream()
        .filter(Objects::nonNull)
        .filter(subject -> !subject.isBlank())
        .map(subject -> "(from:" + from + " subject:\"" + escape(subject) + "\")")
        .toList();
  }

  private String escape(String value) {
    return value.replace("\"", "\\\"");
  }
}
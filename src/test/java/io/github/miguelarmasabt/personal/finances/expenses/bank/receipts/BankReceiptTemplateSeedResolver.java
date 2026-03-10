package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts;

import io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankReceiptTemplateSeedResolver {

  public static List<BankReceiptTemplateEntity> resolve(BankReceiptTemplateSeed seed) {
    Map<String, Map<String, String>> dictionaries = Optional.ofNullable(seed.getDictionaries())
        .orElse(Map.of());

    return Optional.ofNullable(seed.getTemplates())
        .orElse(List.of())
        .stream()
        .filter(Objects::nonNull)
        .peek(template -> resolveDateReplacements(template, dictionaries))
        .toList();
  }

  private static void resolveDateReplacements(BankReceiptTemplateEntity template,
                                              Map<String, Map<String, String>> dictionaries) {
    if (Objects.isNull(template.getExtraction())
        || Objects.isNull(template.getExtraction().getDate())) {
      return;
    }

    BankReceiptTemplateEntity.DateField date = template.getExtraction().getDate();

    if (Objects.nonNull(date.getReplacements()) && !date.getReplacements().isEmpty()) {
      return;
    }

    Optional.ofNullable(date.getReplacementsRef())
        .map(dictionaries::get)
        .ifPresent(date::setReplacements);
  }
}

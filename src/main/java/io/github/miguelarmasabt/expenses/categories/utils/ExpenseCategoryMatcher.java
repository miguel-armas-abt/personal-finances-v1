package io.github.miguelarmasabt.expenses.categories.utils;

import io.github.miguelarmasabt.expenses.categories.enums.CategoryPattern;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryRecipientPatternResponse;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseCategoryMatcher {

  public static Optional<String> resolveCategory(String recipient, List<ExpenseCategoryResponse> categories) {
    if (StringUtils.isBlank(recipient) || Objects.isNull(categories) || categories.isEmpty()) {
      return Optional.empty();
    }

    String normalizedRecipient = RecipientNormalizer.normalize(recipient);

    return categories.stream()
        .filter(Objects::nonNull)
        .filter(category -> Objects.nonNull(category.getRecipientPatterns()) && !category.getRecipientPatterns().isEmpty())
        .max(Comparator.comparingInt(category -> getBestScore(normalizedRecipient, category)))
        .filter(category -> getBestScore(normalizedRecipient, category) >= 0)
        .map(ExpenseCategoryResponse::getName);
  }

  private static int getBestScore(String normalizedRecipient, ExpenseCategoryResponse category) {
    return category.getRecipientPatterns().stream()
        .filter(Objects::nonNull)
        .filter(pattern -> Objects.nonNull(pattern.getCode()))
        .filter(pattern -> StringUtils.isNotBlank(pattern.getValue()))
        .filter(pattern -> matches(normalizedRecipient, pattern))
        .mapToInt(pattern -> CategoryPattern.fromCode(pattern.getCode()).score(pattern.getValue()))
        .max()
        .orElse(-1);
  }

  private static boolean matches(String normalizedRecipient, ExpenseCategoryRecipientPatternResponse pattern) {
    Pattern regex = CategoryPattern.fromCode(pattern.getCode()).toRegex(pattern.getValue());
    return regex.matcher(normalizedRecipient).matches();
  }
}
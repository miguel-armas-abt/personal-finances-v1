package com.demo.service.finances.expenses.categories.utils;

import com.demo.service.finances.expenses.categories.dto.response.ExpenseCategoryResponseDto;
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

  public static Optional<String> resolveCategory(String recipient, List<ExpenseCategoryResponseDto.Category> categories) {
    if (StringUtils.isBlank(recipient) || Objects.isNull(categories) || categories.isEmpty()) {
      return Optional.empty();
    }

    String normalizedRecipient = RecipientNormalizer.normalize(recipient);

    return categories.stream()
        .filter(Objects::nonNull)
        .filter(category -> Objects.nonNull(category.getRecipientPatterns()) && !category.getRecipientPatterns().isEmpty())
        .max(Comparator.comparingInt(category -> getBestScore(normalizedRecipient, category)))
        .filter(category -> getBestScore(normalizedRecipient, category) >= 0)
        .map(ExpenseCategoryResponseDto.Category::getName);
  }

  private static int getBestScore(String normalizedRecipient, ExpenseCategoryResponseDto.Category category) {
    return category.getRecipientPatterns().stream()
        .filter(Objects::nonNull)
        .filter(pattern -> Objects.nonNull(pattern.getCode()))
        .filter(pattern -> StringUtils.isNotBlank(pattern.getValue()))
        .filter(pattern -> matches(normalizedRecipient, pattern))
        .mapToInt(pattern -> pattern.getCode().score(pattern.getValue()))
        .max()
        .orElse(-1);
  }

  private static boolean matches(String normalizedRecipient, ExpenseCategoryResponseDto.RecipientPattern pattern) {
    Pattern regex = pattern.getCode().toRegex(pattern.getValue());
    return regex.matcher(normalizedRecipient).matches();
  }
}
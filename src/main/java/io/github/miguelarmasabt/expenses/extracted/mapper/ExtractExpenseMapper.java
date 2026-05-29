package io.github.miguelarmasabt.expenses.extracted.mapper;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.expenses.categories.utils.ExpenseCategoryMatcher;
import io.github.miguelarmasabt.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Objects;

@Mapper(config = MappingConfig.class)
public interface ExtractExpenseMapper {

  default ExtractExpenseResponseDto mapCategory(ExtractExpenseResponseDto expense,
                                                List<ExpenseCategoryResponse> categories) {
    if (Objects.isNull(expense) || Objects.isNull(expense.getDetail())) {
      return expense;
    }

    String recipient = expense.getDetail().getRecipient();
    return ExpenseCategoryMatcher.resolveCategory(recipient, categories)
        .map(category -> {
          expense.getDetail().setCategory(category);
          return expense;
        })
        .orElse(expense);
  }
}

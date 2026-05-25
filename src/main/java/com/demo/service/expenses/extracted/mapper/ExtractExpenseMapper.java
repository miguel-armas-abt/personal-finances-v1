package com.demo.service.expenses.extracted.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import com.demo.service.expenses.categories.utils.ExpenseCategoryMatcher;
import com.demo.service.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Objects;

@Mapper(config = MappingConfig.class)
public interface ExtractExpenseMapper {

  default ExtractExpenseResponseDto mapCategory(ExtractExpenseResponseDto expense,
                                                List<ExpenseCategoryResponseDto.Category> categories) {
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

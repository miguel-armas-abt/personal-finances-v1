package io.github.miguelarmasabt.personal.finances.expenses.categories.mapper;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.categories.utils.ExpenseCategoryMatcher;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseUpdatedResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.sync.dto.response.ExtractExpenseResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import static io.github.miguelarmasabt.personal.finances.expenses.categories.utils.RecipientNormalizer.normalize;

@Mapper(config = MappingConfig.class)
public interface ExpenseCategoryAssignmentMapper {

  default ExtractExpenseResponseDto assignCategory(ExtractExpenseResponseDto expense,
                                                   List<ExpenseCategoryResponse> categories) {
    return Optional.ofNullable(expense)
        .filter(ex -> Objects.nonNull(expense.getDetail()))
        .map(ExtractExpenseResponseDto::getDetail)
        .flatMap(detail -> ExpenseCategoryMatcher.resolveCategory(detail.getRecipient(), categories)
            .map(category -> {
              expense.getDetail().setCategory(category);
              return expense;
            }))
        .orElse(expense);
  }

  default Optional<ExpenseEntity> assignCategory(ExpenseEntity expense,
                                       List<ExpenseCategoryResponse> categories,
                                       boolean overwriteExistingCategories) {

    BiPredicate<String, String> isSameCategory = (currentCategory, newCategory) ->
        normalize(currentCategory).equalsIgnoreCase(normalize(newCategory));

    return Optional.ofNullable(expense)
        .filter(ex -> Objects.nonNull(ex.getDetail()))
        .map(ExpenseEntity::getDetail)
        .filter(expenseDetail -> StringUtils.isNotBlank(expenseDetail.getRecipient()))
        .filter(detail -> overwriteExistingCategories || StringUtils.isBlank(detail.getCategory()))
        .flatMap(detail -> ExpenseCategoryMatcher.resolveCategory(detail.getRecipient(), categories)
            .filter(resolvedCategory -> !isSameCategory.test(detail.getCategory(), resolvedCategory)))
        .map(category -> {
          expense.getDetail().setCategory(category);
          return expense;
        });
  }

  default ExpenseUpdatedResponseDto toUpdatedResponse(List<String> updatedExpenseIds) {
    ExpenseUpdatedResponseDto response = new ExpenseUpdatedResponseDto();
    response.setIds(updatedExpenseIds);
    return response;
  }

  ExpenseSearchCriteria toSearchCriteria(String userCode, Instant from);
}

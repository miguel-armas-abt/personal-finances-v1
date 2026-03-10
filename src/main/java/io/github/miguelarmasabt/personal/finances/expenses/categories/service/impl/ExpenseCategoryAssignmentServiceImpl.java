package io.github.miguelarmasabt.personal.finances.expenses.categories.service.impl;

import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryAssignmentRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.mapper.ExpenseCategoryAssignmentMapper;
import io.github.miguelarmasabt.personal.finances.expenses.categories.service.ExpenseCategoryAssignmentService;
import io.github.miguelarmasabt.personal.finances.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseUpdatedResponseDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpenseCategoryAssignmentServiceImpl implements ExpenseCategoryAssignmentService {

  private static final int CATEGORY_ASSIGNMENT_CONCURRENCY = 10;

  private final ExpenseRepository expenseRepository;
  private final ExpenseCategoryService categoryService;
  private final ExpenseCategoryAssignmentMapper assignmentMapper;

  @Override
  public Uni<ExpenseUpdatedResponseDto> assignCategories(String userCode,
                                                         ExpenseCategoryAssignmentRequestDto assignmentRequest) {
    Instant from = assignmentRequest.getFrom().toInstant();
    boolean overwriteExistingCategories = assignmentRequest.isOverwriteExistingCategories();

    return categoryService.findAllAssignableCategories(userCode)
        .flatMap(categories -> expenseRepository.search(assignmentMapper.toSearchCriteria(userCode, from))
            .select().where(expense -> Objects.nonNull(expense) && Objects.nonNull(expense.getId()))
            .onItem().transformToMulti(expense -> assignCategoriesIfNeeded(expense, overwriteExistingCategories, categories))
            .merge(CATEGORY_ASSIGNMENT_CONCURRENCY)
            .collect().asList()
            .map(assignmentMapper::toUpdatedResponse));
  }

  private Multi<String> assignCategoriesIfNeeded(ExpenseEntity expense,
                                                 boolean overwriteExistingCategories,
                                                 List<ExpenseCategoryResponse> categories) {

    return assignmentMapper.assignCategory(expense, categories, overwriteExistingCategories)
        .map(resolvedExpense -> expenseRepository.updateById(expense.getId().toString(), resolvedExpense)
            .replaceWith(expense.getId().toString())
            .toMulti())
        .orElseGet(() -> Multi.createFrom().empty());
  }
}
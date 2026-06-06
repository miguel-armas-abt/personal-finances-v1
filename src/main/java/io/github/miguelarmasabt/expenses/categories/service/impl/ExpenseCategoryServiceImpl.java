package io.github.miguelarmasabt.expenses.categories.service.impl;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.expenses.categories.helper.ExpenseCategoryMonthlyTotalResolver;
import io.github.miguelarmasabt.expenses.categories.mapper.ExpenseCategoryMapper;
import io.github.miguelarmasabt.expenses.categories.repository.ExpenseCategoryRepository;
import io.github.miguelarmasabt.expenses.categories.repository.entity.ExpenseCategoryEntity;
import io.github.miguelarmasabt.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

  private final ExpenseCategoryRepository categoryRepository;
  private final ExpenseCategoryMapper mapper;
  private final ExpenseRepository expenseRepository;
  private final ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver;

  @Override
  public Uni<ExpenseCategoryResponseDto> findAllCategories(String userCode) {
    return categoryRepository.findById(userCode)
        .flatMap(entity -> resolveCategoriesWithExcess(userCode, entity));
  }

  @Override
  public Uni<List<ExpenseCategoryResponse>> findAllAssignableCategories(String userCode) {
    return findAllCategories(userCode)
        .map(categories -> Optional.ofNullable(categories)
            .map(ExpenseCategoryResponseDto::getCategories)
            .orElse(List.of())
            .stream()
            .filter(Objects::nonNull)
            .filter(category -> StringUtils.isNotBlank(category.getName()))
            .filter(category -> Objects.nonNull(category.getRecipientPatterns()))
            .filter(category -> !category.getRecipientPatterns().isEmpty())
            .toList());
  }

  @Override
  public Uni<Void> updateCategories(String userCode, ExpenseCategoryRequestDto categoryRequest) {
    return categoryRepository.updateByUserCode(mapper.toEntity(userCode, categoryRequest));
  }

  private Uni<ExpenseCategoryResponseDto> resolveCategoriesWithExcess(String userCode,
                                                                      ExpenseCategoryEntity categoryEntity) {
    if (Objects.isNull(categoryEntity) ||
        Objects.isNull(categoryEntity.getCategories()) ||
        categoryEntity.getCategories().isEmpty()) {
      return Uni.createFrom().item(mapper.empty());
    }

    ExpenseCategoryResponseDto categoryResponse = mapper.toResponseDto(categoryEntity);
    String calculationCurrency = Optional.ofNullable(categoryEntity.getCurrency()).orElse(Currency.PEN.name());

    return expenseRepository.getCurrentMonthTotalsByCategory(userCode)
        .flatMap(monthlyTotals -> monthlyTotalResolver.resolveTotalsInCalculationCurrency(calculationCurrency, monthlyTotals))
        .map(homologatedTotals -> mapper.mapExcess(categoryResponse, homologatedTotals));
  }
}
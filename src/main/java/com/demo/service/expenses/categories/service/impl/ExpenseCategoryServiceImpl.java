package com.demo.service.expenses.categories.service.impl;

import com.demo.service.commons.enums.Currency;
import com.demo.service.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import com.demo.service.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import com.demo.service.expenses.categories.helper.ExpenseCategoryMonthlyTotalResolver;
import com.demo.service.expenses.categories.mapper.ExpenseCategoryMapper;
import com.demo.service.expenses.categories.repository.ExpenseCategoryRepository;
import com.demo.service.expenses.categories.repository.entity.ExpenseCategoryEntity;
import com.demo.service.expenses.categories.service.ExpenseCategoryService;
import com.demo.service.expenses.crud.repository.ExpenseRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

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
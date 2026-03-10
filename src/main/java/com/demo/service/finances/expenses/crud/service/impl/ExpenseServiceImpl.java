package com.demo.service.finances.expenses.crud.service.impl;

import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.finances.expenses.crud.dto.params.ExpenseQueryParams;
import com.demo.service.finances.expenses.crud.dto.params.ExpenseSearchParams;
import com.demo.service.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import com.demo.service.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseResponseDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseSaveResponseDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseSearchResponseDto;
import com.demo.service.finances.expenses.crud.mapper.ExpenseSaveMapper;
import com.demo.service.finances.expenses.crud.mapper.ExpenseSearchMapper;
import com.demo.service.finances.expenses.crud.mapper.ExpenseUpdateMapper;
import com.demo.service.finances.expenses.crud.repository.ExpenseRepository;
import com.demo.service.finances.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import com.demo.service.finances.expenses.crud.repository.entity.ExpenseEntity;
import com.demo.service.finances.expenses.crud.service.ExpenseService;
import com.demo.service.finances.expenses.crud.utils.CursorEncoder;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@ApplicationScoped
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseSaveMapper saveMapper;
  private final ExpenseSearchMapper searchMapper;
  private final ExpenseUpdateMapper updateMapper;
  private final ApplicationProperties properties;

  @Override
  public Uni<ExpenseSaveResponseDto> saveExpense(String userCode, ExpenseSaveRequestDto saveRequest) {
    ExpenseEntity expense = saveMapper.toEntity(userCode, saveRequest);
    return expenseRepository.save(expense)
        .map(saveMapper::toResponse);
  }

  @Override
  public Uni<Void> updateExpense(String userCode, String expenseId, ExpenseUpdateRequestDto updateRequest) {
    ExpenseEntity expense = updateMapper.toUpdateEntity(userCode, updateRequest);
    return expenseRepository.updateById(expenseId, expense);
  }

  @Override
  public Uni<Void> deleteExpense(String userCode, String expenseId) {
    return expenseRepository.deleteById(expenseId);
  }

  @Override
  public Multi<ExpenseResponseDto> searchExpenses(String userCode, ExpenseQueryParams params) {
    ExpenseSearchCriteria searchCriteria = searchMapper.toSearchCriteria(userCode, params);
    return expenseRepository.search(searchCriteria)
        .map(searchMapper::toResponseDto);
  }

  @Override
  public Uni<ExpenseSearchResponseDto> searchExpensesByCursorPagination(String userCode, ExpenseSearchParams params) {
    int limit = properties.features().searchCriteria().limit();
    ExpenseSearchCriteria searchCriteria = searchMapper.toSearchCriteria(userCode, params);

    CursorEncoder.Cursor cursor = null;

    if (Objects.nonNull(params.getEncodedCursor())) {
      cursor = CursorEncoder.decode(params.getEncodedCursor());
    }

    return expenseRepository.searchBySortPagination(searchCriteria, limit, cursor)
        .collect().asList()
        .map(expenses -> searchMapper.toSearchResponseDto(expenses, limit));
  }
}

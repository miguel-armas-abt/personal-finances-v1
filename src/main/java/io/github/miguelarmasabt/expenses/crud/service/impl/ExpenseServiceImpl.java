package io.github.miguelarmasabt.expenses.crud.service.impl;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseSaveMapper;
import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseSearchMapper;
import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseUpdateMapper;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.crud.service.ExpenseService;
import io.github.miguelarmasabt.expenses.crud.utils.CursorEncoder;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSearchResponseDto;
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
    int pageSize = properties.features().searchCriteria().pageSize();
    ExpenseSearchCriteria searchCriteria = searchMapper.toSearchCriteria(userCode, params);

    CursorEncoder.Cursor cursor = null;

    if (Objects.nonNull(params) && Objects.nonNull(params.getEncodedCursor())) {
      cursor = CursorEncoder.decode(params.getEncodedCursor());
    }

    return expenseRepository.searchBySortPagination(searchCriteria, pageSize, cursor)
        .collect().asList()
        .map(expenses -> searchMapper.toSearchResponseDto(expenses, pageSize));
  }
}

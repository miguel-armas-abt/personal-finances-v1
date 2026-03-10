package io.github.miguelarmasabt.personal.finances.expenses.crud.service;

import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSearchResponseDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

public interface ExpenseService {

  Uni<ExpenseSaveResponseDto> saveExpense(String userCode, @Valid ExpenseSaveRequestDto saveRequest);

  Uni<Void> updateExpense(String userCode, String expenseId, @Valid ExpenseUpdateRequestDto updateRequest);

  Uni<Void> deleteExpense(String userCode, String expenseId);

  Multi<ExpenseResponseDto> searchExpenses(String userCode, ExpenseQueryParams params);

  Uni<ExpenseSearchResponseDto> searchExpensesByCursorPagination(String userCode, ExpenseSearchParams params);
}

package io.github.miguelarmasabt.expenses.crud.service;

import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSearchResponseDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ExpenseService {

  Uni<ExpenseSaveResponseDto> saveExpense(String userCode, ExpenseSaveRequestDto saveRequest);

  Uni<Void> updateExpense(String userCode, String expenseId, ExpenseUpdateRequestDto updateRequest);

  Uni<Void> deleteExpense(String userCode, String expenseId);

  Multi<ExpenseResponseDto> searchExpenses(String userCode, ExpenseQueryParams params);

  Uni<ExpenseSearchResponseDto> searchExpensesByCursorPagination(String userCode, ExpenseSearchParams params);
}

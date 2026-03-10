package com.demo.service.finances.expenses.crud.service;

import com.demo.service.finances.expenses.crud.dto.params.ExpenseQueryParams;
import com.demo.service.finances.expenses.crud.dto.params.ExpenseSearchParams;
import com.demo.service.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import com.demo.service.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseResponseDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseSaveResponseDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseSearchResponseDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ExpenseService {

  Uni<ExpenseSaveResponseDto> saveExpense(String userCode, ExpenseSaveRequestDto saveRequest);

  Uni<Void> updateExpense(String userCode, String expenseId, ExpenseUpdateRequestDto updateRequest);

  Uni<Void> deleteExpense(String userCode, String expenseId);

  Multi<ExpenseResponseDto> searchExpenses(String userCode, ExpenseQueryParams params);

  Uni<ExpenseSearchResponseDto> searchExpensesByCursorPagination(String userCode, ExpenseSearchParams params);
}

package io.github.miguelarmasabt.expenses.refresh.service;

import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseRefreshResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseRefreshService {

  Uni<ExpenseRefreshResponseDto> refreshExpenses(String userCode);
}

package io.github.miguelarmasabt.expenses.sync.service;

import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseRefreshResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseSyncService {

  Uni<ExpenseRefreshResponseDto> syncExpenses(String userCode);
}

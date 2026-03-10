package io.github.miguelarmasabt.personal.finances.expenses.sync.service;

import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseRefreshResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseSyncService {

  Uni<ExpenseRefreshResponseDto> syncExpenses(String userCode);
}

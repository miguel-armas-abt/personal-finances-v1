package com.demo.service.finances.expenses.crud.service;

import io.smallrye.mutiny.Uni;

public interface ExpenseRefreshService {

  Uni<Void> refreshExpenses(String userCode);
}

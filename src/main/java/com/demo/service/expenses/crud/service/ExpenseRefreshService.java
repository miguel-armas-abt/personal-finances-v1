package com.demo.service.expenses.crud.service;

import io.smallrye.mutiny.Uni;

public interface ExpenseRefreshService {

  Uni<Void> refreshExpenses(String userCode);
}

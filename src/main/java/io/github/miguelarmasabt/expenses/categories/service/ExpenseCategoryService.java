package io.github.miguelarmasabt.expenses.categories.service;

import io.github.miguelarmasabt.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseCategoryService {

  Uni<ExpenseCategoryResponseDto> findAllCategories(String userCode);

  Uni<Void> updateCategories(String userCode, ExpenseCategoryRequestDto categoryRequest);
}
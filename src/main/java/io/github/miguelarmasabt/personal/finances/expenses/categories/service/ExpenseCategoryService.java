package io.github.miguelarmasabt.personal.finances.expenses.categories.service;

import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

import java.util.List;

public interface ExpenseCategoryService {

  Uni<ExpenseCategoryResponseDto> findAllCategories(String userCode);

  Uni<List<ExpenseCategoryResponse>> findAllAssignableCategories(String userCode);

  Uni<Void> updateCategories(String userCode, @Valid ExpenseCategoryRequestDto categoryRequest);
}
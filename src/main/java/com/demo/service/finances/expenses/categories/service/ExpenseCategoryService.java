package com.demo.service.finances.expenses.categories.service;

import com.demo.service.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import com.demo.service.finances.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseCategoryService {

  Uni<ExpenseCategoryResponseDto> findAllCategories(String userCode);

  Uni<Void> updateCategories(String userCode, ExpenseCategoryRequestDto categoryRequest);
}
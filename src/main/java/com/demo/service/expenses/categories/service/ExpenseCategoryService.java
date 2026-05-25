package com.demo.service.expenses.categories.service;

import com.demo.service.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import com.demo.service.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseCategoryService {

  Uni<ExpenseCategoryResponseDto> findAllCategories(String userCode);

  Uni<Void> updateCategories(String userCode, ExpenseCategoryRequestDto categoryRequest);
}
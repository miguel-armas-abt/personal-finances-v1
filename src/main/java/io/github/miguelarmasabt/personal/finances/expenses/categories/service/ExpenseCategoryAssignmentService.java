package io.github.miguelarmasabt.personal.finances.expenses.categories.service;

import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryAssignmentRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseUpdatedResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

public interface ExpenseCategoryAssignmentService {

  Uni<ExpenseUpdatedResponseDto> assignCategories(String userCode,
                                                  @Valid ExpenseCategoryAssignmentRequestDto assignmentRequest);
}

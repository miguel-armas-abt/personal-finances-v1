package io.github.miguelarmasabt.expenses.categories.service;

import io.github.miguelarmasabt.expenses.categories.dto.request.ExpenseCategoryAssignmentRequestDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseUpdatedResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExpenseCategoryAssignmentService {

  Uni<ExpenseUpdatedResponseDto> assignCategories(String userCode,
                                                  ExpenseCategoryAssignmentRequestDto assignmentRequest);
}

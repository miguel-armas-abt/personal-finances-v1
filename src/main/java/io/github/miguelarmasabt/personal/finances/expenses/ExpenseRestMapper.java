package io.github.miguelarmasabt.personal.finances.expenses;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryAssignmentRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface ExpenseRestMapper {

  ExpenseSaveRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSaveRequestDto saveExpenseRequest);
  ExpenseUpdateRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseUpdateRequestDto updateExpenseRequest);
  ExpenseCategoryAssignmentRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryAssignmentRequestDto expenseCategoryAssignmentRequest);
  ExpenseCategoryRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryRequestDto updateCategoryRequest);
}

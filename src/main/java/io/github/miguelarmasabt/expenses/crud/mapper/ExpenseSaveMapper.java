package io.github.miguelarmasabt.expenses.crud.mapper;

import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.expenses.sync.dto.response.ExtractExpenseResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, imports = {
    DateUtil.class
})
public interface ExpenseSaveMapper {

  @Mapping(target = "date", expression = "java(DateUtil.toInstantAtTime(saveRequest.getDate()))")
  ExpenseEntity toEntity(String userCode, ExpenseSaveRequestDto saveRequest);

  ExpenseSaveResponseDto toResponse(String expenseId);

  ExpenseEntity toEntityFromExtracted(String userCode, ExtractExpenseResponseDto extractedExpense);
}

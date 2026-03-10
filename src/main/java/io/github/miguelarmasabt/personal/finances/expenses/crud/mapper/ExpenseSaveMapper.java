package io.github.miguelarmasabt.personal.finances.expenses.crud.mapper;

import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.sync.dto.response.ExtractExpenseResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, imports = {
    DateUtil.class
})
public interface ExpenseSaveMapper {

//  @Mapping(target = "date", expression = "java(DateUtil.toInstantAtTime(saveRequest.getDate()))")
  ExpenseEntity toEntity(String userCode, ExpenseSaveRequestDto saveRequest);

  ExpenseSaveResponseDto toResponse(String expenseId);

  ExpenseEntity toEntityFromExtracted(String userCode, ExtractExpenseResponseDto extractedExpense);
}

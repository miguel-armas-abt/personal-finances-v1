package com.demo.service.expenses.crud.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.commons.utils.DateUtil;
import com.demo.service.expenses.crud.dto.request.ExpenseSaveRequestDto;
import com.demo.service.expenses.crud.dto.response.ExpenseSaveResponseDto;
import com.demo.service.expenses.crud.repository.entity.ExpenseEntity;
import com.demo.service.expenses.extracted.dto.response.ExtractExpenseResponseDto;
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

package com.demo.service.expenses.csv.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.expenses.crud.dto.request.ExpenseSaveRequestDto;
import com.demo.service.expenses.crud.dto.response.ExpenseResponseDto;
import com.demo.service.expenses.csv.dto.ExpenseCsvRowDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface ExportExpenseCsvMapper {

  @Mapping(target = "gmailMessageId", source = "row.gmailMessageId")
  @Mapping(target = "date", source = "row.date")
  @Mapping(target = "currency", source = "row.currency")
  @Mapping(target = "amount", source = "row.amount")
  @Mapping(target = "detail.category", source = "row.category")
  @Mapping(target = "detail.comments", source = "row.comments")
  @Mapping(target = "detail.recipient", source = "row.recipient")
  @Mapping(target = "detail.source", source = "row.source")
  ExpenseSaveRequestDto toSaveRequest(ExpenseCsvRowDto row);

  @Mapping(target = "date", source = "date.utc")
  @Mapping(target = "source", source = "detail.source")
  @Mapping(target = "currency", source = "currency.code")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "category", source = "detail.category")
  @Mapping(target = "comments", source = "detail.comments")
  @Mapping(target = "recipient", source = "detail.recipient")
  ExpenseCsvRowDto toCsvRow(ExpenseResponseDto expense);
}
package io.github.miguelarmasabt.personal.finances.expenses.csv.mapper;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.csv.dto.ExpenseCsvRowDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseResponseDto;
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
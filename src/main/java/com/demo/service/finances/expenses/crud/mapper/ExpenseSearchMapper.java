package com.demo.service.finances.expenses.crud.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.commons.enums.Currency;
import com.demo.service.commons.utils.DateUtil;
import com.demo.service.finances.expenses.crud.dto.params.ExpenseQueryParams;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseResponseDto;
import com.demo.service.finances.expenses.crud.dto.response.ExpenseSearchResponseDto;
import com.demo.service.finances.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import com.demo.service.finances.expenses.crud.repository.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Mapper(config = MappingConfig.class, imports = {
    DateTimeFormatter.class,
    DateUtil.class,
    Currency.class,
})
public interface ExpenseSearchMapper {

  @Mapping(target = "userCode", source = "userCode")
  @Mapping(target = "from", source = "params.from", qualifiedByName = "toInstant")
  @Mapping(target = "to", source = "params.to", qualifiedByName = "toInstant")
  ExpenseSearchCriteria toSearchCriteria(String userCode, ExpenseQueryParams params);

  @Mapping(target = "id", expression = "java(expense.getId().toString())")
  @Mapping(target = "date.utc", source = "expense.date")
  @Mapping(target = "date.formatted", expression = "java(DateUtil.toSpanishTextDate(expenseEntity.getDate()))")
  @Mapping(target = "currency.code", source = "currency")
  @Mapping(target = "currency.symbol", expression = "java(Currency.valueOf(expenseEntity.getCurrency()).getSymbol())")
  ExpenseResponseDto toResponseDto(ExpenseEntity expense);

  @Named("toInstant")
  static Instant toInstant(String date) {
    return Optional.ofNullable(date)
        .map(d -> DateUtil.toInstantAtTime(date))
        .orElse(null);
  }

  default ExpenseSearchResponseDto toSearchResponseDto(List<ExpenseEntity> expenses, int limit) {
    List<ExpenseResponseDto> data = expenses.stream()
        .map(this::toResponseDto)
        .toList();

    String nextCursor = null;

    if (expenses.size() == limit) {
      nextCursor = expenses.get(expenses.size() - 1).getId().toString();
    }

    return ExpenseSearchResponseDto.builder()
        .expenses(data)
        .nextCursor(nextCursor)
        .build();
  }
}

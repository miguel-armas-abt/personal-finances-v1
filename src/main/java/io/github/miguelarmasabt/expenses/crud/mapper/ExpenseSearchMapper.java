package io.github.miguelarmasabt.expenses.crud.mapper;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSearchResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Mapper(config = MappingConfig.class, imports = {
    DateTimeFormatter.class,
    DateUtil.class,
    Currency.class,
})
public interface ExpenseSearchMapper {

  @Named("toInstant")
  static Instant toInstant(String date) {
    return Optional.ofNullable(date)
        .map(d -> DateUtil.toInstantAtTime(date))
        .orElse(null);
  }

  ExpenseSearchParams toSearchParams(String recipient, String category, String currency, Date from, Date to, String cursor);

  ExpenseQueryParams toQueryParams(String recipient, String category, String currency, Date from, Date to);

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

  default ExpenseSearchResponseDto toSearchResponseDto(List<ExpenseEntity> expenses, int limit) {
    List<ExpenseResponseDto> data = expenses.stream()
        .map(this::toResponseDto)
        .toList();

    String nextCursor = null;

    if (expenses.size() == limit) {
      nextCursor = expenses.get(expenses.size() - 1).getId().toString();
    }

    ExpenseSearchResponseDto response = new ExpenseSearchResponseDto();
    response.setExpenses(data);
    response.setNextCursor(nextCursor);
    return response;
  }
}

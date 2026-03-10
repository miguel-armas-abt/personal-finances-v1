package io.github.miguelarmasabt.personal.finances.expenses.crud.mapper;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.personal.finances.expenses.crud.utils.CursorEncoder;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseResponseCurrency;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSearchResponseDto;
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
    ExpenseResponseCurrency.class,
})
public interface ExpenseSearchMapper {

  @Named("toInstant")
  static Instant toInstant(String date) {
    return Optional.ofNullable(date)
        .map(d -> DateUtil.toInstantAtTime(date))
        .orElse(null);
  }

  ExpenseSearchParams toSearchParams(String recipient, String category, String currency, String from, String to, String encodedCursor);

  ExpenseQueryParams toQueryParams(String recipient, String category, String currency, String from, String to);

  @Mapping(target = "userCode", source = "userCode")
  @Mapping(target = "from", source = "params.from", qualifiedByName = "toInstant")
  @Mapping(target = "to", source = "params.to", qualifiedByName = "toInstant")
  ExpenseSearchCriteria toSearchCriteria(String userCode, ExpenseQueryParams params);

  default ExpenseResponseCurrency toCurrency(String code) {
    Currency currency = Currency.fromCodeOrUnknown(code);
    ExpenseResponseCurrency response = new ExpenseResponseCurrency();
    response.setCode(currency.name());
    response.setSymbol(currency.getSymbol());
    return response;
  }
  @Mapping(target = "id", expression = "java(expense.getId().toString())")
  @Mapping(target = "date.utc", source = "expense.date")
  @Mapping(target = "date.formatted", expression = "java(DateUtil.toSpanishTextDate(expenseEntity.getDate()))")
  @Mapping(target = "currency", expression = "java(toCurrency(expense.getCurrency()))")
  ExpenseResponseDto toResponseDto(ExpenseEntity expense);

  default ExpenseSearchResponseDto toSearchResponseDto(List<ExpenseEntity> expenses, int limit) {
    boolean hasMore = expenses.size() > limit;
    List<ExpenseEntity> page = hasMore ? expenses.subList(0, limit) : expenses;
    List<ExpenseResponseDto> data = page.stream()
        .map(this::toResponseDto)
        .toList();

    String nextCursor = null;

    if (hasMore) {
      ExpenseEntity lastExpense = page.get(page.size() - 1);
      nextCursor = CursorEncoder.encode(lastExpense.getDate(), lastExpense.getId());
    }

    ExpenseSearchResponseDto response = new ExpenseSearchResponseDto();
    response.setExpenses(data);
    response.setNextCursor(nextCursor);
    return response;
  }
}

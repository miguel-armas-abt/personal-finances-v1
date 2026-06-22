package io.github.miguelarmasabt.personal.finances.expenses.categories.mapper;

import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.repository.entity.ExpenseCategoryEntity;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mapper(
    config = MappingConfig.class,
    imports = {Currency.class})
public interface ExpenseCategoryMapper {

  default ExpenseCategoryResponseDto empty() {
    ExpenseCategoryResponseDto category = new ExpenseCategoryResponseDto();
    category.setCategories(List.of());
    return category;
  }

  @Mapping(target = "currency.code", source = "currency")
  @Mapping(target = "currency.symbol", expression = "java(Currency.valueOf(expenseCategoryEntity.getCurrency()).getSymbol())")
  ExpenseCategoryResponseDto toResponseDto(ExpenseCategoryEntity expenseCategoryEntity);

  @Mapping(target = "limit.amount", source = "limit")
  ExpenseCategoryResponse toCategory(ExpenseCategoryEntity.Category category);

  ExpenseCategoryEntity toEntity(String userCode, ExpenseCategoryRequestDto categoryRequest);

  default ExpenseCategoryResponseDto mapExcess(ExpenseCategoryResponseDto categoryResponse,
                                               Map<String, BigDecimal> monthlyTotals) {
    List<ExpenseCategoryResponse> categories = categoryResponse.getCategories();

    categories.forEach(category -> {
      if (Objects.nonNull(category.getLimit())) {
        BigDecimal limit = Optional.ofNullable(category.getLimit().getAmount())
            .map(BigDecimal::new)
            .orElse(BigDecimal.ZERO);

        BigDecimal spent = monthlyTotals.getOrDefault(category.getName(), BigDecimal.ZERO);

        boolean isExceeded = spent.compareTo(limit) > 0;
        BigDecimal balance = limit.subtract(spent);

        category.getLimit().setBalance(balance.toPlainString());
        category.getLimit().setIsExceeded(isExceeded);
      }
    });
    categoryResponse.setCategories(categories);
    return categoryResponse;
  }
}
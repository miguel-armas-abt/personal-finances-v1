package com.demo.service.finances.expenses.categories.mapper;

import com.demo.commons.config.mapper.MappingConfig;
import com.demo.service.commons.enums.Currency;
import com.demo.service.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import com.demo.service.finances.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import com.demo.service.finances.expenses.categories.repository.entity.ExpenseCategoryEntity;
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
  ExpenseCategoryResponseDto.Category toCategory(ExpenseCategoryEntity.Category category);

  ExpenseCategoryEntity toEntity(String userCode, ExpenseCategoryRequestDto categoryRequest);

  default ExpenseCategoryResponseDto mapExcess(ExpenseCategoryResponseDto categoryResponse,
                                               Map<String, BigDecimal> monthlyTotals) {
    List<ExpenseCategoryResponseDto.Category> categories = categoryResponse.getCategories();

    categories.forEach(category -> {
      if (Objects.nonNull(category.getLimit())) {
        BigDecimal limit = Optional.ofNullable(category.getLimit().getAmount()).orElse(BigDecimal.ZERO);
        BigDecimal spent = monthlyTotals.getOrDefault(category.getName(), BigDecimal.ZERO);

        boolean isExceeded = spent.compareTo(limit) > 0;
        BigDecimal balance = limit.subtract(spent);

        category.getLimit().setBalance(balance);
        category.getLimit().setIsExceeded(isExceeded);
      }
    });
    categoryResponse.setCategories(categories);
    return categoryResponse;
  }
}
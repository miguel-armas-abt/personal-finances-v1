package io.github.miguelarmasabt.personal.finances.expenses.categories.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.helper.ExpenseCategoryMonthlyTotalResolver;
import io.github.miguelarmasabt.personal.finances.expenses.categories.mapper.ExpenseCategoryMapper;
import io.github.miguelarmasabt.personal.finances.expenses.categories.repository.ExpenseCategoryRepository;
import io.github.miguelarmasabt.personal.finances.expenses.categories.repository.entity.ExpenseCategoryEntity;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.aggregation.MonthlyCategoryTotalAggregation;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.github.miguelarmasabt.tools.JsonSerializer;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ExpenseCategoryServiceImplTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
  private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer(OBJECT_MAPPER);

  private static final String USER_CODE = "user-001";

  @Test
  @DisplayName("Given existing expense categories, when finding all categories, then return categories with monthly excess")
  void givenExistingExpenseCategories_WhenFindAllCategories_ThenReturnCategoriesWithMonthlyExcess() {
    // Arrange
    ExpenseCategoryRepository categoryRepository = Mock.categoryRepository();
    ExpenseRepository expenseRepository = Mock.expenseRepository();
    ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver = Mock.monthlyTotalResolver();
    ExpenseCategoryServiceImpl service = Mock.expenseCategoryService(
        categoryRepository,
        expenseRepository,
        monthlyTotalResolver
    );
    ExpenseCategoryEntity entity = expenseCategoryEntity();
    MonthlyCategoryTotalAggregation monthlyTotals = monthlyCategoryTotalAggregation();

    when(categoryRepository.findById(USER_CODE))
        .thenReturn(Uni.createFrom().item(entity));
    when(expenseRepository.getCurrentMonthTotalsByCategory(USER_CODE))
        .thenReturn(Uni.createFrom().item(monthlyTotals));
    when(monthlyTotalResolver.resolveTotalsInCalculationCurrency(eq(Currency.PEN.name()), eq(monthlyTotals)))
        .thenReturn(Uni.createFrom().item(Map.of(
            "Food", new BigDecimal("125.50"),
            "Transport", new BigDecimal("40.00")
        )));

    // Act
    ExpenseCategoryResponseDto actual = service.findAllCategories(USER_CODE)
        .await()
        .indefinitely();

    // Assert
    verify(categoryRepository).findById(USER_CODE);
    verify(expenseRepository).getCurrentMonthTotalsByCategory(USER_CODE);
    verify(monthlyTotalResolver).resolveTotalsInCalculationCurrency(Currency.PEN.name(), monthlyTotals);

    assertThat(actual.getCurrency().getCode()).isEqualTo(Currency.PEN.name());
    assertThat(actual.getCurrency().getSymbol()).isEqualTo(Currency.PEN.getSymbol());
    assertThat(actual.getCategories())
        .extracting(ExpenseCategoryResponse::getName)
        .containsExactly("Food", "Transport");
    assertThat(actual.getCategories().getFirst().getLimit().getAmount()).isEqualTo("100.00");
    assertThat(actual.getCategories().getFirst().getLimit().getBalance()).isEqualTo("-25.50");
    assertThat(actual.getCategories().getFirst().getLimit().getIsExceeded()).isTrue();
    assertThat(actual.getCategories().get(1).getLimit().getBalance()).isEqualTo("10.00");
    assertThat(actual.getCategories().get(1).getLimit().getIsExceeded()).isFalse();
  }

  @Test
  @DisplayName("Given no expense category entity, when finding all categories, then return an empty response")
  void givenNoExpenseCategoryEntity_WhenFindAllCategories_ThenReturnEmptyResponse() {
    // Arrange
    ExpenseCategoryRepository categoryRepository = Mock.categoryRepository();
    ExpenseRepository expenseRepository = Mock.expenseRepository();
    ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver = Mock.monthlyTotalResolver();
    ExpenseCategoryServiceImpl service = Mock.expenseCategoryService(
        categoryRepository,
        expenseRepository,
        monthlyTotalResolver
    );

    when(categoryRepository.findById(USER_CODE))
        .thenReturn(Uni.createFrom().nullItem());

    // Act
    ExpenseCategoryResponseDto actual = service.findAllCategories(USER_CODE)
        .await()
        .indefinitely();

    // Assert
    verify(categoryRepository).findById(USER_CODE);
    verifyNoInteractions(expenseRepository, monthlyTotalResolver);

    assertThat(actual.getCategories()).isEmpty();
    assertThat(actual.getCurrency()).isNull();
  }

  @Test
  @DisplayName("Given mixed expense categories, when finding assignable categories, then return categories with recipient patterns")
  void givenMixedExpenseCategories_WhenFindAllAssignableCategories_ThenReturnCategoriesWithRecipientPatterns() {
    // Arrange
    ExpenseCategoryRepository categoryRepository = Mock.categoryRepository();
    ExpenseRepository expenseRepository = Mock.expenseRepository();
    ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver = Mock.monthlyTotalResolver();
    ExpenseCategoryServiceImpl service = Mock.expenseCategoryService(
        categoryRepository,
        expenseRepository,
        monthlyTotalResolver
    );
    ExpenseCategoryEntity entity = mixedExpenseCategoryEntity();
    MonthlyCategoryTotalAggregation monthlyTotals = new MonthlyCategoryTotalAggregation(List.of());

    when(categoryRepository.findById(USER_CODE))
        .thenReturn(Uni.createFrom().item(entity));
    when(expenseRepository.getCurrentMonthTotalsByCategory(USER_CODE))
        .thenReturn(Uni.createFrom().item(monthlyTotals));
    when(monthlyTotalResolver.resolveTotalsInCalculationCurrency(Currency.PEN.name(), monthlyTotals))
        .thenReturn(Uni.createFrom().item(Map.of()));

    // Act
    List<ExpenseCategoryResponse> actual = service.findAllAssignableCategories(USER_CODE)
        .await()
        .indefinitely();

    // Assert
    assertThat(actual)
        .singleElement()
        .satisfies(category -> {
          assertThat(category.getName()).isEqualTo("Food");
          assertThat(category.getRecipientPatterns())
              .singleElement()
              .satisfies(pattern -> {
                assertThat(pattern.getCode()).isEqualTo("CONTAINS");
                assertThat(pattern.getValue()).isEqualTo("Coffee");
              });
        });
  }

  @Test
  @DisplayName("Given a valid category request, when updating categories, then update the entity by user code")
  void givenValidCategoryRequest_WhenUpdateCategories_ThenUpdateEntityByUserCode() {
    // Arrange
    ExpenseCategoryRepository categoryRepository = Mock.categoryRepository();
    ExpenseCategoryServiceImpl service = Mock.expenseCategoryService(
        categoryRepository,
        Mock.expenseRepository(),
        Mock.monthlyTotalResolver()
    );
    ExpenseCategoryRequestDto request = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/categories/ExpenseCategoryRequestDto.json",
        ExpenseCategoryRequestDto.class
    );

    when(categoryRepository.updateByUserCode(any(ExpenseCategoryEntity.class)))
        .thenReturn(Uni.createFrom().voidItem());

    // Act
    Void actual = service.updateCategories(USER_CODE, request)
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<ExpenseCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ExpenseCategoryEntity.class);

    verify(categoryRepository).updateByUserCode(categoryCaptor.capture());

    assertThat(actual).isNull();
    assertThat(categoryCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expenseCategoryEntity());
  }

  private static ExpenseCategoryEntity expenseCategoryEntity() {
    return new ExpenseCategoryEntity(
        USER_CODE,
        Currency.PEN.name(),
        List.of(
            new ExpenseCategoryEntity.Category(
                "Food",
                new BigDecimal("100.00"),
                List.of(new ExpenseCategoryEntity.RecipientPattern("CONTAINS", "Coffee"))
            ),
            new ExpenseCategoryEntity.Category(
                "Transport",
                new BigDecimal("50.00"),
                List.of(new ExpenseCategoryEntity.RecipientPattern("START_WITH", "Taxi"))
            )
        )
    );
  }

  private static ExpenseCategoryEntity mixedExpenseCategoryEntity() {
    return new ExpenseCategoryEntity(
        USER_CODE,
        Currency.PEN.name(),
        List.of(
            new ExpenseCategoryEntity.Category(
                "Food",
                new BigDecimal("100.00"),
                List.of(new ExpenseCategoryEntity.RecipientPattern("CONTAINS", "Coffee"))
            ),
            new ExpenseCategoryEntity.Category(" ", new BigDecimal("25.00"), List.of()),
            new ExpenseCategoryEntity.Category("Transport", new BigDecimal("50.00"), List.of()),
            new ExpenseCategoryEntity.Category("Health", new BigDecimal("75.00"), null)
        )
    );
  }

  private static MonthlyCategoryTotalAggregation monthlyCategoryTotalAggregation() {
    return new MonthlyCategoryTotalAggregation(List.of(
        new MonthlyCategoryTotalAggregation.ExpenseCategory(
            "Food",
            List.of(new MonthlyCategoryTotalAggregation.CurrencyTotal(Currency.PEN.name(), new BigDecimal("125.50")))
        ),
        new MonthlyCategoryTotalAggregation.ExpenseCategory(
            "Transport",
            List.of(new MonthlyCategoryTotalAggregation.CurrencyTotal(Currency.PEN.name(), new BigDecimal("40.00")))
        )
    ));
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Mock {

    static ExpenseCategoryServiceImpl expenseCategoryService(ExpenseCategoryRepository categoryRepository,
                                                             ExpenseRepository expenseRepository,
                                                             ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver) {
      ExpenseCategoryMapper mapper = Mappers.getMapper(ExpenseCategoryMapper.class);

      return new ExpenseCategoryServiceImpl(categoryRepository, mapper, expenseRepository, monthlyTotalResolver);
    }

    static ExpenseCategoryRepository categoryRepository() {
      return Mockito.mock(ExpenseCategoryRepository.class);
    }

    static ExpenseRepository expenseRepository() {
      return Mockito.mock(ExpenseRepository.class);
    }

    static ExpenseCategoryMonthlyTotalResolver monthlyTotalResolver() {
      return Mockito.mock(ExpenseCategoryMonthlyTotalResolver.class);
    }
  }
}

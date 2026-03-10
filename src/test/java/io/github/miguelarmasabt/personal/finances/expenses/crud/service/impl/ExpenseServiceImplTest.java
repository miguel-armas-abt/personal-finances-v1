package io.github.miguelarmasabt.personal.finances.expenses.crud.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.miguelarmasabt.commons.enums.Currency;
import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.properties.business.BusinessProperties;
import io.github.miguelarmasabt.commons.properties.business.expenses.ExpenseProperties;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.mapper.ExpenseSaveMapper;
import io.github.miguelarmasabt.personal.finances.expenses.crud.mapper.ExpenseSearchMapper;
import io.github.miguelarmasabt.personal.finances.expenses.crud.mapper.ExpenseUpdateMapper;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.criteria.ExpenseSearchCriteria;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.personal.finances.expenses.crud.utils.CursorEncoder;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSaveResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSearchResponseDto;
import io.github.miguelarmasabt.tools.JsonSerializer;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceImplTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
  private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer(OBJECT_MAPPER);

  private static final String USER_CODE = "user-001";
  private static final String EXPENSE_ID = "64f0f7a9a0f1b2c3d4e5f601";
  private static final Instant EXPENSE_DATE = Instant.parse("2026-07-10T15:30:00Z");
  private static final int PAGE_SIZE = 2;

  @Test
  @DisplayName("Given a valid expense save request, when saving the expense, then return the saved expense ID")
  void givenValidExpenseSaveRequest_WhenSaveExpense_ThenReturnSavedExpenseId() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);
    ExpenseSaveRequestDto request = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/crud/ExpenseSaveRequestDto.json",
        ExpenseSaveRequestDto.class
    );

    when(repository.save(any(ExpenseEntity.class)))
        .thenReturn(Uni.createFrom().item(EXPENSE_ID));

    // Act
    ExpenseSaveResponseDto actual = service.saveExpense(USER_CODE, request)
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<ExpenseEntity> expenseCaptor = ArgumentCaptor.forClass(ExpenseEntity.class);

    verify(repository).save(expenseCaptor.capture());

    assertThat(actual.getExpenseId()).isEqualTo(EXPENSE_ID);
    assertThat(expenseCaptor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expenseEntity());
  }

  @Test
  @DisplayName("Given a valid expense update request, when updating the expense, then update it by ID")
  void givenValidExpenseUpdateRequest_WhenUpdateExpense_ThenUpdateExpenseById() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);
    ExpenseUpdateRequestDto request = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/crud/ExpenseUpdateRequestDto.json",
        ExpenseUpdateRequestDto.class
    );

    when(repository.updateById(eq(EXPENSE_ID), any(ExpenseEntity.class)))
        .thenReturn(Uni.createFrom().voidItem());

    // Act
    Void actual = service.updateExpense(USER_CODE, EXPENSE_ID, request)
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<ExpenseEntity> expenseCaptor = ArgumentCaptor.forClass(ExpenseEntity.class);

    verify(repository).updateById(eq(EXPENSE_ID), expenseCaptor.capture());

    assertThat(actual).isNull();
    assertThat(expenseCaptor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("id", "gmailMessageId", "detail.source")
        .isEqualTo(expenseEntity());
  }

  @Test
  @DisplayName("Given a valid expense ID, when deleting the expense, then delete it by ID")
  void givenValidExpenseId_WhenDeleteExpense_ThenDeleteExpenseById() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);

    when(repository.deleteById(EXPENSE_ID))
        .thenReturn(Uni.createFrom().voidItem());

    // Act
    Void actual = service.deleteExpense(USER_CODE, EXPENSE_ID)
        .await()
        .indefinitely();

    // Assert
    verify(repository).deleteById(EXPENSE_ID);

    assertThat(actual).isNull();
  }

  @Test
  @DisplayName("Given expense query params, when searching expenses, then return matching expenses")
  void givenExpenseQueryParams_WhenSearchExpenses_ThenReturnMatchingExpenses() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);
    ExpenseQueryParams params = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/crud/ExpenseQueryParams.json",
        ExpenseQueryParams.class
    );
    ExpenseEntity expense = expenseEntity();

    when(repository.search(any(ExpenseSearchCriteria.class)))
        .thenReturn(Multi.createFrom().item(expense));

    // Act
    List<ExpenseResponseDto> actual = service.searchExpenses(USER_CODE, params)
        .collect()
        .asList()
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<ExpenseSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ExpenseSearchCriteria.class);

    verify(repository).search(criteriaCaptor.capture());

    assertThat(criteriaCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expenseSearchCriteria());
    assertThat(actual)
        .singleElement()
        .satisfies(response -> {
          assertThat(response.getId()).isEqualTo(EXPENSE_ID);
          assertThat(response.getCurrency().getCode()).isEqualTo(Currency.PEN.name());
          assertThat(response.getCurrency().getSymbol()).isEqualTo(Currency.PEN.getSymbol());
          assertThat(response.getDetail().getRecipient()).isEqualTo("Coffee Shop");
          assertThat(response.getAmount()).isEqualTo("25.50");
        });
  }

  @Test
  @DisplayName("Given search params without cursor, when searching by cursor pagination, then return the first page")
  void givenSearchParamsWithoutCursor_WhenSearchExpensesByCursorPagination_ThenReturnFirstPage() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);
    ExpenseSearchParams params = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/crud/ExpenseSearchParams.json",
        ExpenseSearchParams.class
    );
    ExpenseEntity expense = expenseEntity();

    when(repository.searchBySortPagination(any(ExpenseSearchCriteria.class), eq(PAGE_SIZE), eq(null)))
        .thenReturn(Multi.createFrom().item(expense));

    // Act
    ExpenseSearchResponseDto actual = service.searchExpensesByCursorPagination(USER_CODE, params)
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<ExpenseSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ExpenseSearchCriteria.class);

    verify(repository).searchBySortPagination(criteriaCaptor.capture(), eq(PAGE_SIZE), eq(null));

    assertThat(criteriaCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expenseSearchCriteria());
    assertThat(actual.getExpenses())
        .singleElement()
        .extracting(ExpenseResponseDto::getId)
        .isEqualTo(EXPENSE_ID);
    assertThat(actual.getNextCursor()).isNull();
  }

  @Test
  @DisplayName("Given search params with cursor, when searching by cursor pagination, then decode the cursor")
  void givenSearchParamsWithCursor_WhenSearchExpensesByCursorPagination_ThenDecodeCursor() {
    // Arrange
    ExpenseRepository repository = Mock.expenseRepository();
    ExpenseServiceImpl service = Mock.expenseService(repository);
    ObjectId cursorId = new ObjectId("64f0f7a9a0f1b2c3d4e5f699");
    ExpenseSearchParams params = JSON_SERIALIZER.readElementFromFile(
        "fixtures/expenses/crud/ExpenseSearchParams.json",
        ExpenseSearchParams.class
    );
    params.setEncodedCursor(CursorEncoder.encode(EXPENSE_DATE, cursorId));
    ExpenseEntity firstExpense = expenseEntity("64f0f7a9a0f1b2c3d4e5f602");
    ExpenseEntity secondExpense = expenseEntity("64f0f7a9a0f1b2c3d4e5f603");

    when(repository.searchBySortPagination(any(ExpenseSearchCriteria.class), eq(PAGE_SIZE), any(CursorEncoder.Cursor.class)))
        .thenReturn(Multi.createFrom().items(firstExpense, secondExpense));

    // Act
    ExpenseSearchResponseDto actual = service.searchExpensesByCursorPagination(USER_CODE, params)
        .await()
        .indefinitely();

    // Assert
    ArgumentCaptor<CursorEncoder.Cursor> cursorCaptor = ArgumentCaptor.forClass(CursorEncoder.Cursor.class);

    verify(repository).searchBySortPagination(any(ExpenseSearchCriteria.class), eq(PAGE_SIZE), cursorCaptor.capture());

    assertThat(cursorCaptor.getValue().date()).isEqualTo(EXPENSE_DATE);
    assertThat(cursorCaptor.getValue().id()).isEqualTo(cursorId);
    assertThat(actual.getExpenses())
        .extracting(ExpenseResponseDto::getId)
        .containsExactly(firstExpense.getId().toString(), secondExpense.getId().toString());
    assertThat(actual.getNextCursor()).isEqualTo(secondExpense.getId().toString());
  }

  private static ExpenseSearchCriteria expenseSearchCriteria() {
    ExpenseSearchCriteria criteria = new ExpenseSearchCriteria();
    criteria.setUserCode(USER_CODE);
    criteria.setRecipient("Coffee Shop");
    criteria.setCategory("Food");
    criteria.setCurrency(Currency.PEN.name());
    criteria.setFrom(Instant.parse("2026-07-01T00:00:00Z"));
    criteria.setTo(Instant.parse("2026-07-10T00:00:00Z"));
    return criteria;
  }

  private static ExpenseEntity expenseEntity() {
    return expenseEntity(EXPENSE_ID);
  }

  private static ExpenseEntity expenseEntity(String expenseId) {
    ExpenseEntity.ExpenseDetail detail = new ExpenseEntity.ExpenseDetail(
        "Food",
        "Latte",
        "Coffee Shop",
        "MANUAL"
    );

    return new ExpenseEntity(
        new ObjectId(expenseId),
        USER_CODE,
        "gmail-message-001",
        EXPENSE_DATE,
        detail,
        Currency.PEN.name(),
        new BigDecimal("25.50")
    );
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Mock {

    static ExpenseServiceImpl expenseService(ExpenseRepository repository) {
      ExpenseSaveMapper saveMapper = Mappers.getMapper(ExpenseSaveMapper.class);
      ExpenseSearchMapper searchMapper = Mappers.getMapper(ExpenseSearchMapper.class);
      ExpenseUpdateMapper updateMapper = Mappers.getMapper(ExpenseUpdateMapper.class);
      ApplicationProperties properties = applicationProperties();

      return new ExpenseServiceImpl(repository, saveMapper, searchMapper, updateMapper, properties);
    }

    static ExpenseRepository expenseRepository() {
      return Mockito.mock(ExpenseRepository.class);
    }

    static ApplicationProperties applicationProperties() {
      ApplicationProperties properties = Mockito.mock(ApplicationProperties.class);
      BusinessProperties businessProperties = Mockito.mock(BusinessProperties.class);
      ExpenseProperties expenseProperties = Mockito.mock(ExpenseProperties.class);
      ExpenseProperties.SearchCriteria searchCriteria = Mockito.mock(ExpenseProperties.SearchCriteria.class);

      when(properties.business()).thenReturn(businessProperties);
      when(businessProperties.expenses()).thenReturn(expenseProperties);
      when(expenseProperties.searchCriteria()).thenReturn(searchCriteria);
      when(searchCriteria.pageSize()).thenReturn(PAGE_SIZE);

      return properties;
    }
  }
}

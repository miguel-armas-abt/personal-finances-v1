package io.github.miguelarmasabt.expenses;

import io.github.miguelarmasabt.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseSearchMapper;
import io.github.miguelarmasabt.expenses.crud.service.ExpenseRefreshService;
import io.github.miguelarmasabt.expenses.crud.service.ExpenseService;
import io.github.miguelarmasabt.expenses.csv.service.ExportExpenseCsvService;
import io.github.miguelarmasabt.expenses.csv.utils.FileNameGenerator;
import io.github.miguelarmasabt.expenses.rest.server.ExpensesResource;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseSearchResponseDto;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestMulti;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpensesRestService implements ExpensesResource {

  private final ExpenseSearchMapper searchMapper;
  private final ExpenseService expenseService;
  private final ExpenseRefreshService refreshService;
  private final ExportExpenseCsvService exportExpenseCsvService;
  private final ExpenseCategoryService categoryService;

  @Override
  public CompletionStage<ExpenseSearchResponseDto> searchExpenses(String authorization,
                                                                  String callerName,
                                                                  String traceParent,
                                                                  String userCode,
                                                                  String recipient,
                                                                  String category,
                                                                  String currency,
                                                                  Date from,
                                                                  Date to,
                                                                  String encodedCursor) {
    ExpenseSearchParams params = searchMapper.toSearchParams(recipient, category, currency, from, to, encodedCursor);
    return expenseService.searchExpensesByCursorPagination(userCode, params)
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Response> saveExpense(String callerName,
                                               String traceParent,
                                               String userCode,
                                               ExpenseSaveRequestDto saveRequest) {
    return expenseService.saveExpense(userCode, saveRequest)
        .map(response -> Response.status(Response.Status.CREATED)
            .entity(response)
            .location(URI.create("/expenses/" + response.getExpenseId()))
            .build())
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Void> updateExpense(String authorization,
                                             String callerName,
                                             String traceParent,
                                             String userCode,
                                             String expenseId,
                                             ExpenseUpdateRequestDto updateRequest) {
    return expenseService.updateExpense(userCode, expenseId, updateRequest)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Void> deleteExpense(String callerName,
                                             String traceParent,
                                             String userCode,
                                             String expenseId) {
    return expenseService.deleteExpense(userCode, expenseId)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Void> refreshExpenses(String authorization,
                                               String callerName,
                                               String traceParent,
                                               String userCode) {
    return refreshService.refreshExpenses(userCode)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }

  @Override
  public RestMulti<Buffer> exportCsv(String authorization,
                                     String callerName,
                                     String traceParent,
                                     String userCode,
                                     String recipient,
                                     String category,
                                     String currency,
                                     Date from,
                                     Date to) {
    ExpenseQueryParams params = searchMapper.toQueryParams(recipient, category, currency, from, to);
    return RestMulti.fromMultiData(exportExpenseCsvService.exportCsv(userCode, params))
        .header("Content-Disposition", "attachment; filename=\"" + FileNameGenerator.generateFileName() + "\"")
        .header("Content-Type", "text/csv; charset=UTF-8")
        .build();
  }

  @Override
  public CompletionStage<ExpenseCategoryResponseDto> findAllCategories(String callerName,
                                                                       String traceParent,
                                                                       String userCode) {
    return categoryService.findAllCategories(userCode)
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Void> updateExpenseCategories(String authorization,
                                                       String callerName,
                                                       String traceParent,
                                                       String userCode,
                                                       ExpenseCategoryRequestDto updateCategoryRequest) {
    return categoryService.updateCategories(userCode, updateCategoryRequest)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }
}

package io.github.miguelarmasabt.personal.finances.expenses;

import io.github.miguelarmasabt.commons.dto.params.AppHeaders;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryAssignmentRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.categories.service.ExpenseCategoryAssignmentService;
import io.github.miguelarmasabt.personal.finances.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseSearchParams;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseSaveRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.mapper.ExpenseSearchMapper;
import io.github.miguelarmasabt.personal.finances.expenses.crud.service.ExpenseService;
import io.github.miguelarmasabt.personal.finances.expenses.csv.service.ExportExpenseCsvService;
import io.github.miguelarmasabt.personal.finances.expenses.csv.service.ImportExpenseCsvService;
import io.github.miguelarmasabt.personal.finances.expenses.csv.utils.FileNameGenerator;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.ExpensesResource;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.ExpenseSearchResponseDto;
import io.github.miguelarmasabt.personal.finances.expenses.sync.service.ExpenseSyncService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestMulti;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpenseRestService implements ExpensesResource {

  private final ExpenseSearchMapper searchMapper;
  private final ExpenseService expenseService;
  private final ExpenseSyncService syncService;
  private final ExportExpenseCsvService exportExpenseCsvService;
  private final ImportExpenseCsvService importExpenseCsvService;
  private final ExpenseCategoryService categoryService;
  private final ExpenseCategoryAssignmentService assignmentService;

  @Override
  public CompletionStage<ExpenseSearchResponseDto> searchExpenses(
      String authorization,
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
  public CompletionStage<Response> saveExpense(
      String authorization,
      String callerName,
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
  public CompletionStage<Void> updateExpense(
      String authorization,
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
  public CompletionStage<Void> deleteExpense(
      String authorization,
      String callerName,
      String traceParent,
      String userCode,
      String expenseId) {

    return expenseService.deleteExpense(userCode, expenseId)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Response> syncExpenses(
      String authorization,
      String gmailAccessToken,
      String callerName,
      String traceParent,
      String userCode) {

    return syncService.syncExpenses(userCode)
        .map(response -> Objects.isNull(response.getGmailMessageIds()) || response.getGmailMessageIds().isEmpty()
            ? Response.noContent().build()
            : Response.ok(response).build())
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Response> assignCategories(
      String authorization,
      String callerName,
      String traceParent,
      String userCode,
      ExpenseCategoryAssignmentRequestDto assignmentRequest) {

    return assignmentService.assignCategories(userCode, assignmentRequest)
        .map(response -> Objects.isNull(response.getIds()) || response.getIds().isEmpty()
            ? Response.noContent().build()
            : Response.ok(response).build())
        .subscribeAsCompletionStage();
  }

  @Override
  public RestMulti<Buffer> exportCsv(
      String authorization,
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

  @POST
  @Path("/csv/import")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Uni<Response> importCsv(@BeanParam @Valid AppHeaders headers,
                                 @RestForm("file") FileUpload file) {
    return importExpenseCsvService.importCsv(headers.getUserCode(), file)
        .replaceWith(Response.status(Response.Status.CREATED).build());
  }

  @Override
  public CompletionStage<ExpenseCategoryResponseDto> findAllCategories(
      String authorization,
      String callerName,
      String traceParent,
      String userCode) {

    return categoryService.findAllCategories(userCode)
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Void> updateExpenseCategories(
      String authorization,
      String callerName,
      String traceParent,
      String userCode,
      ExpenseCategoryRequestDto updateCategoryRequest) {

    return categoryService.updateCategories(userCode, updateCategoryRequest)
        .replaceWithVoid()
        .subscribeAsCompletionStage();
  }
}

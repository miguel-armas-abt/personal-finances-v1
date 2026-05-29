//package io.github.miguelarmasabt.expenses;
//
//import io.github.miguelarmasabt.commons.dto.params.AppHeaders;
//import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseSearchParams;
//import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseSaveRequestDto;
//import io.github.miguelarmasabt.expenses.crud.dto.request.ExpenseUpdateRequestDto;
//import io.github.miguelarmasabt.expenses.crud.service.ExpenseRefreshService;
//import io.github.miguelarmasabt.expenses.crud.service.ExpenseService;
//import io.smallrye.mutiny.Uni;
//import jakarta.validation.Valid;
//import jakarta.ws.rs.BeanParam;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.DELETE;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.PUT;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.PathParam;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import lombok.RequiredArgsConstructor;
//
//import java.net.URI;
//
//@Path("/expenses")
//@RequiredArgsConstructor
//public class ExpenseRestService {
//
//  private final ExpenseService expenseService;
//  private final ExpenseRefreshService refreshService;
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  public Uni<Response> searchExpenses(@Valid @BeanParam AppHeaders headers,
//                                      @Valid @BeanParam ExpenseSearchParams searchParams) {
//    return expenseService.searchExpensesByCursorPagination(headers.getUserCode(), searchParams)
//        .map(result -> Response.ok(result).build());
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  public Uni<Response> saveExpense(@Valid @BeanParam AppHeaders headers,
//                                   @Valid ExpenseSaveRequestDto saveRequest) {
//    return expenseService.saveExpense(headers.getUserCode(), saveRequest)
//        .map(response -> Response.status(Response.Status.CREATED)
//            .entity(response)
//            .location(URI.create("/expenses/" + response.getExpenseId()))
//            .build());
//  }
//
//  @PUT
//  @Path("/{expenseId}")
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  public Uni<Response> updateExpense(@Valid @BeanParam AppHeaders headers,
//                                     @PathParam("expenseId") String expenseId,
//                                     @Valid ExpenseUpdateRequestDto updateRequest) {
//    return expenseService.updateExpense(headers.getUserCode(), expenseId, updateRequest)
//        .replaceWith(Response.noContent().build());
//  }
//
//  @DELETE()
//  @Path("/{expenseId}")
//  public Uni<Response> deleteExpense(@Valid @BeanParam AppHeaders headers,
//                                     @PathParam("expenseId") String expenseId) {
//    return expenseService.deleteExpense(headers.getUserCode(), expenseId)
//        .replaceWith(Response.noContent().build());
//  }
//
//  @POST
//  @Path("/refresh")
//  @Produces(MediaType.APPLICATION_JSON)
//  public Uni<Response> refreshExpenses(@Valid @BeanParam AppHeaders headers) {
//    return refreshService.refreshExpenses(headers.getUserCode())
//        .map(result -> Response.noContent().build());
//  }
//}

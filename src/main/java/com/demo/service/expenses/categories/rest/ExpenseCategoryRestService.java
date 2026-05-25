package com.demo.service.expenses.categories.rest;

import com.demo.service.commons.dto.params.AppHeaders;
import com.demo.service.expenses.categories.dto.request.ExpenseCategoryRequestDto;
import com.demo.service.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import com.demo.service.expenses.categories.service.ExpenseCategoryService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryRestService {

  private final ExpenseCategoryService categoryService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> updateCategories(@BeanParam @Valid AppHeaders headers,
                                        @Valid ExpenseCategoryRequestDto categoryRequest) {
    return categoryService.updateCategories(headers.getUserCode(), categoryRequest)
        .map(response -> Response.status(Response.Status.NO_CONTENT).build());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<ExpenseCategoryResponseDto> findAllCategories(@BeanParam @Valid AppHeaders headers) {
    return categoryService.findAllCategories(headers.getUserCode());
  }
}

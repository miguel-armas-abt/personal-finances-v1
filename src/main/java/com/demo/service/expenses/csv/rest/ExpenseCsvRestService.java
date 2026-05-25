package com.demo.service.expenses.csv.rest;

import com.demo.service.commons.dto.params.AppHeaders;
import com.demo.service.expenses.crud.dto.params.ExpenseQueryParams;
import com.demo.service.expenses.csv.service.ExportExpenseCsvService;
import com.demo.service.expenses.csv.service.ImportExpenseCsvService;
import com.demo.service.expenses.csv.utils.FileNameGenerator;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestMulti;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/expenses")
@RequiredArgsConstructor
public class ExpenseCsvRestService {

  private final ImportExpenseCsvService importExpenseCsvService;
  private final ExportExpenseCsvService exportExpenseCsvService;

  @POST
  @Path("/csv/import")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Uni<Response> importCsv(@BeanParam @Valid AppHeaders headers,
                                 @RestForm("file") FileUpload file) {
    return importExpenseCsvService.importCsv(headers.getUserCode(), file)
        .replaceWith(Response.status(Response.Status.CREATED).build());
  }

  @GET
  @Path("/csv/export")
  @Produces("text/csv")
  public RestMulti<Buffer> exportCsv(@BeanParam @Valid AppHeaders headers,
                                     @Valid @BeanParam ExpenseQueryParams searchQueryParams) {
    return RestMulti.fromMultiData(exportExpenseCsvService.exportCsv(headers.getUserCode(), searchQueryParams))
        .header("Content-Disposition", "attachment; filename=\"" + FileNameGenerator.generateFileName() + "\"")
        .header("Content-Type", "text/csv; charset=UTF-8")
        .build();
  }
}

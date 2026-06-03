package io.github.miguelarmasabt.expenses.csv.service.impl;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.github.miguelarmasabt.expenses.crud.service.ExpenseService;
import io.github.miguelarmasabt.expenses.csv.helper.ExportExpenseCsvEncoder;
import io.github.miguelarmasabt.expenses.csv.mapper.ExportExpenseCsvMapper;
import io.github.miguelarmasabt.expenses.csv.service.ExportExpenseCsvService;
import io.smallrye.mutiny.Multi;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class ExportExpenseCsvServiceImpl implements ExportExpenseCsvService {

  private final ExpenseService expenseService;
  private final ExportExpenseCsvMapper csvMapper;
  private final ExportExpenseCsvEncoder csvEncoder;
  private final ApplicationProperties properties;

  @Override
  public Multi<Buffer> exportCsv(String userCode, ExpenseQueryParams searchQueryParams) {
    Multi<Buffer> header = Multi.createFrom().item(csvEncoder.encodeHeader());

    Multi<Buffer> body = expenseService.searchExpenses(userCode, searchQueryParams)
        .onItem().transform(csvMapper::toCsvRow)
        .group().intoLists().of(properties.features().expenses().csv().exports().rowsPerChunk())
        .onItem().transform(csvEncoder::encodeRows);

    return Multi.createBy().concatenating().streams(header, body);
  }
}
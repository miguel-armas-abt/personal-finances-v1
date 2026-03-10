package com.demo.service.finances.expenses.csv.service.impl;

import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.finances.expenses.crud.dto.params.ExpenseQueryParams;
import com.demo.service.finances.expenses.crud.service.ExpenseService;
import com.demo.service.finances.expenses.csv.helper.ExportExpenseCsvEncoder;
import com.demo.service.finances.expenses.csv.mapper.ExportExpenseCsvMapper;
import com.demo.service.finances.expenses.csv.service.ExportExpenseCsvService;
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
        .group().intoLists().of(properties.features().csv().exports().rowsPerChunk())
        .onItem().transform(csvEncoder::encodeRows);

    return Multi.createBy().concatenating().streams(header, body);
  }
}
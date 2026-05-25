package com.demo.service.expenses.csv.service;

import com.demo.service.expenses.crud.dto.params.ExpenseQueryParams;
import io.smallrye.mutiny.Multi;
import io.vertx.core.buffer.Buffer;

public interface ExportExpenseCsvService {

  Multi<Buffer> exportCsv(String userCode, ExpenseQueryParams searchQueryParams);
}

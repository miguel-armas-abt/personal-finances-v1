package io.github.miguelarmasabt.expenses.csv.service;

import io.github.miguelarmasabt.expenses.crud.dto.params.ExpenseQueryParams;
import io.smallrye.mutiny.Multi;
import io.vertx.core.buffer.Buffer;

public interface ExportExpenseCsvService {

  Multi<Buffer> exportCsv(String userCode, ExpenseQueryParams searchQueryParams);
}

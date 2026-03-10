package io.github.miguelarmasabt.personal.finances.expenses.csv.service;

import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.params.ExpenseQueryParams;
import io.smallrye.mutiny.Multi;
import io.vertx.core.buffer.Buffer;

public interface ExportExpenseCsvService {

  Multi<Buffer> exportCsv(String userCode, ExpenseQueryParams searchQueryParams);
}

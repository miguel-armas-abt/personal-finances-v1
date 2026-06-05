package io.github.miguelarmasabt.expenses.bank.receipts.service;

import io.github.miguelarmasabt.expenses.sync.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

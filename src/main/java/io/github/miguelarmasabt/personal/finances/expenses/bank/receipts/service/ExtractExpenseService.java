package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.service;

import io.github.miguelarmasabt.personal.finances.expenses.sync.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

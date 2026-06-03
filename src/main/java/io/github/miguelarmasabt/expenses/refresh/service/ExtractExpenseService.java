package io.github.miguelarmasabt.expenses.refresh.service;

import io.github.miguelarmasabt.expenses.refresh.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

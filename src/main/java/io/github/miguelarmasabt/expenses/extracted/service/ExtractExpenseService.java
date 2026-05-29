package io.github.miguelarmasabt.expenses.extracted.service;

import io.github.miguelarmasabt.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

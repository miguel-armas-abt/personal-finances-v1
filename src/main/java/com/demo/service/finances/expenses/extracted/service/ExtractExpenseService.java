package com.demo.service.finances.expenses.extracted.service;

import com.demo.service.finances.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

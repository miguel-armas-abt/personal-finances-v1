package com.demo.service.expenses.extracted.service;

import com.demo.service.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.smallrye.mutiny.Multi;

public interface ExtractExpenseService {

  Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode);
}

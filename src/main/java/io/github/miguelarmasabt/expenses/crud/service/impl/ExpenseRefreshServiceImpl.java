package io.github.miguelarmasabt.expenses.crud.service.impl;

import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseSaveMapper;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.crud.service.ExpenseRefreshService;
import io.github.miguelarmasabt.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.extracted.service.ExtractExpenseService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class ExpenseRefreshServiceImpl implements ExpenseRefreshService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseSaveMapper saveMapper;
  private final ExtractExpenseService extractService;

  @Override
  public Uni<Void> refreshExpenses(String userCode) {
    return extractService.getExtractedExpenses(userCode)
        .onItem().transformToUniAndMerge(extractedExpense -> saveIfNotExists(extractedExpense, userCode))
        .collect().asList()
        .replaceWithVoid();
  }

  private Uni<Void> saveIfNotExists(ExtractExpenseResponseDto extractedExpense, String userCode) {
    return expenseRepository.existsGmailMessage(extractedExpense.getGmailMessageId())
        .flatMap(exists -> {
          if (exists) {
            return Uni.createFrom().voidItem();
          }
          ExpenseEntity entity = saveMapper.toEntityFromExtracted(userCode, extractedExpense);
          return expenseRepository.save(entity)
              .replaceWithVoid();
        });
  }
}
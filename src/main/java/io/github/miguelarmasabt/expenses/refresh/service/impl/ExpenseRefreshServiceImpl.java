package io.github.miguelarmasabt.expenses.refresh.service.impl;

import io.github.miguelarmasabt.commons.repository.sync.checkpoint.SyncCheckpointRepository;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.expenses.crud.mapper.ExpenseSaveMapper;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.crud.repository.entity.ExpenseEntity;
import io.github.miguelarmasabt.expenses.refresh.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.refresh.mapper.ExpenseRefreshMapper;
import io.github.miguelarmasabt.expenses.refresh.service.ExpenseRefreshService;
import io.github.miguelarmasabt.expenses.refresh.service.ExtractExpenseService;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseRefreshResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@ApplicationScoped
public class ExpenseRefreshServiceImpl implements ExpenseRefreshService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseSaveMapper saveMapper;
  private final ExtractExpenseService extractService;
  private final SyncCheckpointRepository syncCheckpointRepository;
  private final ExpenseRefreshMapper refreshMapper;

  @Override
  public Uni<ExpenseRefreshResponseDto> refreshExpenses(String userCode) {
    return extractService.getExtractedExpenses(userCode)
        .collect().asList()
        .flatMap(extractedExpenses -> saveAll(extractedExpenses, userCode)
            .flatMap(savedGmailMessageIds -> updateCheckpointIfNeeded(userCode, extractedExpenses)
                .replaceWith(refreshMapper.toResponse(savedGmailMessageIds))));
  }

  private Uni<List<String>> saveAll(List<ExtractExpenseResponseDto> extractedExpenses,
                                    String userCode) {
    List<ExpenseEntity> entities = extractedExpenses.stream()
        .map(extractedExpense -> saveMapper.toEntityFromExtracted(userCode, extractedExpense))
        .toList();

    List<String> savedGmailMessageIds = extractedExpenses.stream()
        .map(ExtractExpenseResponseDto::getGmailMessageId)
        .filter(Objects::nonNull)
        .toList();

    return expenseRepository.saveAll(entities)
        .replaceWith(savedGmailMessageIds);
  }

  private Uni<Void> updateCheckpointIfNeeded(String userCode,
                                             List<ExtractExpenseResponseDto> extractedExpenses) {
    return extractedExpenses.stream()
        .filter(Objects::nonNull)
        .map(ExtractExpenseResponseDto::getGmailMessageReceivedAt)
        .filter(Objects::nonNull)
        .max(Instant::compareTo)
        .map(newestMessageReceivedAt ->
            syncCheckpointRepository.updateByUserCode(userCode, SyncScope.EXPENSES, newestMessageReceivedAt))
        .orElseGet(() -> Uni.createFrom().voidItem());
  }
}
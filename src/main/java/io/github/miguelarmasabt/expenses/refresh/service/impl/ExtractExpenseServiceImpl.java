package io.github.miguelarmasabt.expenses.refresh.service.impl;

import io.github.miguelarmasabt.commons.repository.gmail.GmailRepositoryAdapter;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.SyncCheckpointRepository;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.refresh.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.refresh.helper.GmailQueryBuilder;
import io.github.miguelarmasabt.expenses.refresh.mapper.ExtractExpenseMapper;
import io.github.miguelarmasabt.expenses.refresh.service.ExtractExpenseService;
import io.github.miguelarmasabt.expenses.refresh.strategy.BankReceiptExpenseExtractorDispatcher;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.github.miguelarmasabt.repository.gmail.model.MessageResponseWrapper;
import io.github.miguelarmasabt.repository.gmail.model.MessageSummary;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor
public class ExtractExpenseServiceImpl implements ExtractExpenseService {

  private static final int MESSAGE_CONTENT_CONCURRENCY = 10;

  private final SyncCheckpointRepository syncCheckpointRepository;
  private final GmailQueryBuilder gmailQueryBuilder;
  private final BankReceiptExpenseExtractorDispatcher expenseExtractor;
  private final GmailRepositoryAdapter gmailRepository;
  private final ExpenseCategoryService categoryService;
  private final ExtractExpenseMapper mapper;
  private final ExpenseRepository expenseRepository;

  @Override
  public Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode) {
    return syncCheckpointRepository.findOrPersist(userCode, SyncScope.EXPENSES)
        .onItem().transformToMulti(syncCheckpoint -> extractExpenses(userCode, syncCheckpoint.getCheckpointAt()));
  }

  private Multi<ExtractExpenseResponseDto> extractExpenses(String userCode, Instant lastCheckpointAt) {
    String lastMessageReadAt = DateUtil.toGmailDate(lastCheckpointAt);
    String query = gmailQueryBuilder.buildBankReceiptQueryAfter(lastMessageReadAt);

    return categoryService.findAllCategories(userCode)
        .onItem().transformToMulti(categoryResponse ->
            gmailRepository.getMessages(query)
                .onItem().transform(this::resolveMessages)
                .onItem().transformToMulti(messages -> filterNewMessages(userCode, messages))
                .onItem().transformToMulti(message -> this.extractExpenseFromMessage(message, categoryResponse).toMulti())
                .merge(MESSAGE_CONTENT_CONCURRENCY)
                .select().where(Objects::nonNull)
                .select().where(expense -> expense.getGmailMessageReceivedAt().isAfter(lastCheckpointAt))
        );
  }

  private List<MessageSummary> resolveMessages(MessageResponseWrapper response) {
    return Optional.ofNullable(response)
        .map(MessageResponseWrapper::getMessages)
        .orElse(List.of());
  }

  private Multi<MessageSummary> filterNewMessages(String userCode, List<MessageSummary> messages) {
    List<String> gmailMessageIds = messages.stream()
        .filter(Objects::nonNull)
        .map(MessageSummary::getId)
        .filter(Objects::nonNull)
        .toList();

    return expenseRepository.existsGmailMessages(userCode, gmailMessageIds)
        .onItem().transformToMulti(existingGmailMessageIds ->
            Multi.createFrom().iterable(messages)
                .select().where(message -> isNewMessage(message, existingGmailMessageIds))
        );
  }

  private boolean isNewMessage(MessageSummary message, Set<String> existingGmailMessageIds) {
    return Objects.nonNull(message)
        && Objects.nonNull(message.getId())
        && !existingGmailMessageIds.contains(message.getId());
  }

  private Uni<ExtractExpenseResponseDto> extractExpenseFromMessage(MessageSummary message,
                                                                   ExpenseCategoryResponseDto categoryResponse) {
    List<ExpenseCategoryResponse> categories = categoryResponse.getCategories();

    return gmailRepository.getMessageContent(message.getId())
        .flatMap(expenseExtractor::toDto)
        .map(expense -> mapper.mapCategory(expense, categories));
  }
}
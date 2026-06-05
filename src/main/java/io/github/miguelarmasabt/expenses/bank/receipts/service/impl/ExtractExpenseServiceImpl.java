package io.github.miguelarmasabt.expenses.bank.receipts.service.impl;

import io.github.miguelarmasabt.commons.repository.gmail.GmailRepositoryAdapter;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.SyncCheckpointRepository;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.expenses.bank.receipts.helper.BankReceiptExpenseExtractorDispatcher;
import io.github.miguelarmasabt.expenses.bank.receipts.helper.GmailQueryBuilder;
import io.github.miguelarmasabt.expenses.bank.receipts.mapper.ExtractExpenseMapper;
import io.github.miguelarmasabt.expenses.bank.receipts.repository.BankReceiptTemplateRepository;
import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.github.miguelarmasabt.expenses.bank.receipts.service.ExtractExpenseService;
import io.github.miguelarmasabt.expenses.categories.service.ExpenseCategoryService;
import io.github.miguelarmasabt.expenses.crud.repository.ExpenseRepository;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponse;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseCategoryResponseDto;
import io.github.miguelarmasabt.expenses.sync.dto.response.ExtractExpenseResponseDto;
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
  private final BankReceiptTemplateRepository templateRepository;
  private final GmailQueryBuilder gmailQueryBuilder;
  private final BankReceiptExpenseExtractorDispatcher expenseExtractor;
  private final GmailRepositoryAdapter gmailRepository;
  private final ExpenseCategoryService categoryService;
  private final ExpenseRepository expenseRepository;
  private final ExtractExpenseMapper mapper;

  @Override
  public Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode) {
    return syncCheckpointRepository.findOrPersist(userCode, SyncScope.EXPENSES)
        .onItem().transformToMulti(syncCheckpoint -> templateRepository.findEnabled()
            .onItem().transformToMulti(templates -> extractExpenses(userCode, syncCheckpoint.getCheckpointAt(), templates))
        );
  }

  private Multi<ExtractExpenseResponseDto> extractExpenses(String userCode,
                                                           Instant lastCheckpointAt,
                                                           List<BankReceiptTemplateEntity> templates) {
    if (Objects.isNull(templates) || templates.isEmpty()) {
      return Multi.createFrom().empty();
    }

    String lastMessageReadAt = DateUtil.toGmailDate(lastCheckpointAt);
    String query = gmailQueryBuilder.buildBankReceiptQueryAfter(templates, lastMessageReadAt);

    return categoryService.findAllCategories(userCode)
        .onItem().transformToMulti(categoryResponse ->
            gmailRepository.getMessages(query)
                .onItem().transform(this::resolveMessages)
                .flatMap(messages -> filterNotPersistedMessages(userCode, messages))
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .onItem().transformToMulti(message ->
                    this.extractExpenseFromMessage(message, categoryResponse, templates).toMulti())
                .merge(MESSAGE_CONTENT_CONCURRENCY)
                .select().where(Objects::nonNull)
                .select().where(expense -> expense.getGmailMessageReceivedAt().isAfter(lastCheckpointAt)));
  }

  private Uni<List<MessageSummary>> filterNotPersistedMessages(String userCode,
                                                               List<MessageSummary> messages) {
    List<MessageSummary> validMessages = Optional.ofNullable(messages)
        .orElse(List.of())
        .stream()
        .filter(Objects::nonNull)
        .filter(message -> Objects.nonNull(message.getId()))
        .toList();

    List<String> gmailMessageIds = validMessages.stream()
        .map(MessageSummary::getId)
        .distinct()
        .toList();

    if (gmailMessageIds.isEmpty()) {
      return Uni.createFrom().item(List.of());
    }

    return expenseRepository.existsGmailMessages(userCode, gmailMessageIds)
        .map(existingGmailMessageIds -> {
          Set<String> existingIds = Optional.ofNullable(existingGmailMessageIds)
              .orElse(Set.of());

          return validMessages.stream()
              .filter(message -> !existingIds.contains(message.getId()))
              .toList();
        });
  }

  private List<MessageSummary> resolveMessages(MessageResponseWrapper response) {
    return Optional.ofNullable(response)
        .map(MessageResponseWrapper::getMessages)
        .orElse(List.of());
  }

  private Uni<ExtractExpenseResponseDto> extractExpenseFromMessage(MessageSummary message,
                                                                   ExpenseCategoryResponseDto categoryResponse,
                                                                   List<BankReceiptTemplateEntity> templates) {
    List<ExpenseCategoryResponse> categories = categoryResponse.getCategories();

    return gmailRepository.getMessageContent(message.getId())
        .flatMap(messageContent -> expenseExtractor.toDto(messageContent, templates))
        .map(expense -> mapper.mapCategory(expense, categories));
  }
}
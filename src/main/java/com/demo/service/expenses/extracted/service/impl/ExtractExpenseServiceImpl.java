package com.demo.service.expenses.extracted.service.impl;

import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.commons.repository.gmail.GmailRepository;
import com.demo.service.commons.repository.gmail.wrapper.response.MessageResponseWrapper;
import com.demo.service.commons.repository.user.activity.UserActivityRepository;
import com.demo.service.commons.utils.DateUtil;
import com.demo.service.expenses.categories.dto.response.ExpenseCategoryResponseDto;
import com.demo.service.expenses.categories.service.ExpenseCategoryService;
import com.demo.service.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import com.demo.service.expenses.extracted.helper.GmailParameterHelper;
import com.demo.service.expenses.extracted.mapper.ExtractExpenseMapper;
import com.demo.service.expenses.extracted.service.ExtractExpenseService;
import com.demo.service.expenses.extracted.strategy.ExtractExpenseStrategyDispatcher;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ExtractExpenseServiceImpl implements ExtractExpenseService {

  private static final int MESSAGE_CONTENT_CONCURRENCY = 10;

  private final UserActivityRepository userActivityRepository;
  private final GmailParameterHelper gmailParameterHelper;
  private final ExtractExpenseStrategyDispatcher strategyDispatcher;
  private final ApplicationProperties properties;
  private final ExpenseCategoryService categoryService;
  private final ExtractExpenseMapper mapper;

  @RestClient
  GmailRepository gmailRepository;

  @Override
  public Multi<ExtractExpenseResponseDto> getExtractedExpenses(String userCode) {
    return userActivityRepository.findOrPersist(userCode)
        .onItem().transformToMulti(userActivity -> extractExpenses(userCode, userActivity.getLastSeenAt()));
  }

  private Multi<ExtractExpenseResponseDto> extractExpenses(String userCode, Instant lastSeenAt) {
    String gmailDate = DateUtil.toGmailDate(lastSeenAt);
    String query = gmailParameterHelper.getGmailQuery(gmailDate);
    long pageSize = gmailParameterHelper.getPageSize();
    String fields = properties.features().gmailMessages().fields();
    String format = properties.features().gmailMessageContent().format();

    return categoryService.findAllCategories(userCode)
        .onItem().transformToMulti(categoryResponse ->
            gmailRepository.getMessages(query, pageSize, fields)
                .onItem().transform(message -> Optional.ofNullable(message.getMessages()).orElse(List.of()))
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .onItem().transformToMulti(message -> this.extractExpenseFromMessage(message, format, categoryResponse).toMulti())
                .merge(MESSAGE_CONTENT_CONCURRENCY)
                .select().where(Objects::nonNull)
                .select().where(expense -> expense.getDate().isAfter(lastSeenAt))
                .collect().asList()
                .flatMap(expenses -> updateLastSeenIfNeeded(userCode, lastSeenAt, expenses)
                    .replaceWith(expenses))
                .onItem().transformToMulti(Multi.createFrom()::iterable)
        );
  }

  private Uni<ExtractExpenseResponseDto> extractExpenseFromMessage(MessageResponseWrapper.Message message,
                                                                   String format,
                                                                   ExpenseCategoryResponseDto categoryResponse) {
    List<ExpenseCategoryResponseDto.Category> categories = categoryResponse.getCategories();
    return gmailRepository.getMessageContent(message.getId(), format)
        .flatMap(strategyDispatcher::toDto)
        .map(expense -> mapper.mapCategory(expense, categories));
  }

  private Uni<Void> updateLastSeenIfNeeded(String userCode,
                                           Instant lastSeenAt,
                                           List<ExtractExpenseResponseDto> expenses) {
    return expenses.stream()
        .filter(Objects::nonNull)
        .map(ExtractExpenseResponseDto::getDate)
        .filter(Objects::nonNull)
        .max(Instant::compareTo)
        .filter(newestDate -> newestDate.isAfter(lastSeenAt))
        .map(newestDate -> userActivityRepository.updateByUserCode(userCode, newestDate))
        .orElseGet(() -> Uni.createFrom().voidItem());
  }
}

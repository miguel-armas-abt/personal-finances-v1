package io.github.miguelarmasabt.expenses.refresh.strategy;

import io.github.miguelarmasabt.expenses.refresh.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.refresh.exceptions.DuplicatedStrategyException;
import io.github.miguelarmasabt.expenses.refresh.exceptions.UnsupportedExtractExpenseStrategyException;
import io.github.miguelarmasabt.expenses.refresh.utils.GmailMessageHeaderUtil;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class BankReceiptExpenseExtractorDispatcher {

  private final ExpenseExtractorHelper expenseExtractorHelper;
  private final Map<String, BankReceiptExpenseExtractorStrategy> strategyMap;

  public BankReceiptExpenseExtractorDispatcher(ExpenseExtractorHelper expenseExtractorHelper,
                                               Instance<BankReceiptExpenseExtractorStrategy> strategyInstances) {
    this.expenseExtractorHelper = expenseExtractorHelper;
    this.strategyMap = strategyInstances.stream()
        .collect(Collectors.toUnmodifiableMap(
            BankReceiptExpenseExtractorStrategy::supports,
            Function.identity(),
            (left, right) -> {
              throw new DuplicatedStrategyException(left.supports());
            }
        ));
  }

  public Uni<ExtractExpenseResponseDto> toDto(MessageContentResponseWrapper message) {
    GmailMessageHeaderUtil.GmailMessageHeaders headers = GmailMessageHeaderUtil.extractHeaders(message.getPayload());

    String label = expenseExtractorHelper.resolveLabel(headers.from(), headers.subject())
        .orElseThrow(() -> new UnsupportedExtractExpenseStrategyException(headers.from(), headers.subject()));

    BankReceiptExpenseExtractorStrategy strategy = Optional.ofNullable(strategyMap.get(label))
        .orElseThrow(() -> new UnsupportedExtractExpenseStrategyException(headers.from(), headers.subject()));

    return strategy.toDto(message);
  }
}
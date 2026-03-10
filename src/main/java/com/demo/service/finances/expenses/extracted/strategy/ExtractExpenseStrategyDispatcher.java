package com.demo.service.finances.expenses.extracted.strategy;

import com.demo.service.commons.repository.gmail.wrapper.response.MessageContentResponseWrapper;
import com.demo.service.finances.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import com.demo.service.finances.expenses.extracted.exceptions.DuplicatedStrategyException;
import com.demo.service.finances.expenses.extracted.exceptions.UnsupportedExtractExpenseStrategyException;
import com.demo.service.finances.expenses.extracted.utils.GmailMessageHeaderUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExtractExpenseStrategyDispatcher {

  private final ExtractExpenseHelper extractExpenseHelper;
  private final Map<String, ExtractExpenseStrategy> strategyMap;

  public ExtractExpenseStrategyDispatcher(ExtractExpenseHelper extractExpenseHelper,
                                          Instance<ExtractExpenseStrategy> strategyInstances) {
    this.extractExpenseHelper = extractExpenseHelper;
    this.strategyMap = strategyInstances.stream()
        .collect(Collectors.toUnmodifiableMap(
            ExtractExpenseStrategy::supports,
            Function.identity(),
            (left, right) -> {
              throw new DuplicatedStrategyException(left.supports());
            }
        ));
  }

  public Uni<ExtractExpenseResponseDto> toDto(MessageContentResponseWrapper message) {
    GmailMessageHeaderUtil.GmailMessageHeaders headers = GmailMessageHeaderUtil.extractHeaders(message.getPayload());

    String label = extractExpenseHelper.resolveLabel(headers.from(), headers.subject())
        .orElseThrow(() -> new UnsupportedExtractExpenseStrategyException(headers.from(), headers.subject()));

    ExtractExpenseStrategy strategy = Optional.ofNullable(strategyMap.get(label))
        .orElseThrow(() -> new UnsupportedExtractExpenseStrategyException(headers.from(), headers.subject()));

    return strategy.toDto(message);
  }
}
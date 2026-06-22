package io.github.miguelarmasabt.personal.finances.expenses.categories.helper;

import io.github.miguelarmasabt.commons.repository.exchange.rate.api.ExchangeRateRepository;
import io.github.miguelarmasabt.commons.repository.exchange.rate.model.ExchangeRateResponseWrapper;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.aggregation.MonthlyCategoryTotalAggregation;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpenseCategoryMonthlyTotalResolver {

  @RestClient
  ExchangeRateRepository exchangeRateRepository;

  public Uni<Map<String, BigDecimal>> resolveTotalsInCalculationCurrency(String calculationCurrency,
                                                                         MonthlyCategoryTotalAggregation monthlyTotals) {
    if (Objects.isNull(monthlyTotals) ||
        Objects.isNull(monthlyTotals.getCategories()) ||
        monthlyTotals.getCategories().isEmpty()) {

      return Uni.createFrom().item(Map.of());
    }

    return Multi.createFrom().iterable(monthlyTotals.getCategories())
        .onItem().transformToUniAndMerge(categoryTotal ->
            resolveCategoryTotal(categoryTotal, calculationCurrency)
                .map(total -> Map.entry(categoryTotal.getCategory(), total)))
        .collect().asList()
        .map(entries -> {
          Map<String, BigDecimal> result = new HashMap<>();
          entries.forEach(entry -> result.merge(entry.getKey(), entry.getValue(), BigDecimal::add));
          return result;
        });
  }

  private Uni<BigDecimal> resolveCategoryTotal(MonthlyCategoryTotalAggregation.ExpenseCategory categoryTotal,
                                               String calculationCurrency) {
    List<MonthlyCategoryTotalAggregation.CurrencyTotal> totalsByCurrency = categoryTotal.getTotalsByCurrency();

    return io.smallrye.mutiny.Multi.createFrom().iterable(totalsByCurrency)
        .onItem().transformToUniAndMerge(currencyTotal ->
            convert(currencyTotal.getCurrency(), calculationCurrency, currencyTotal.getTotal())
        )
        .collect().asList()
        .map(values -> values.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
  }

  private Uni<BigDecimal> convert(String sourceCurrency, String targetCurrency, BigDecimal amount) {
    if (Objects.isNull(amount)) {
      return Uni.createFrom().item(BigDecimal.ZERO);
    }

    if (Objects.equals(sourceCurrency, targetCurrency)) {
      return Uni.createFrom().item(amount);
    }

    return exchangeRateRepository.getExchangeRate(sourceCurrency, targetCurrency)
        .map(ExchangeRateResponseWrapper::getRate)
        .map(rate -> amount.multiply(rate).setScale(2, RoundingMode.HALF_UP));
  }
}
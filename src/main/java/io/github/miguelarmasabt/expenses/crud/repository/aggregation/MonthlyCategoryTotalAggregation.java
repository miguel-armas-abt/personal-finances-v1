package io.github.miguelarmasabt.expenses.crud.repository.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyCategoryTotalAggregation implements Serializable {

  private List<ExpenseCategory> categories;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseCategory implements Serializable {
    private String category;
    private List<CurrencyTotal> totalsByCurrency;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CurrencyTotal implements Serializable {
    private String currency;
    private BigDecimal total;
  }
}

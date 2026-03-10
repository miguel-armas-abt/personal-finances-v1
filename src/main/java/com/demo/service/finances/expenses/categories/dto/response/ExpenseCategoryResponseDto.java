package com.demo.service.finances.expenses.categories.dto.response;

import com.demo.service.finances.expenses.categories.enums.CategoryPattern;
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
public class ExpenseCategoryResponseDto implements Serializable {

  private Currency currency;

  private List<Category> categories;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Currency {
    private String code;
    private String symbol;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Category {
    private String name;
    private Limit limit;
    private List<RecipientPattern> recipientPatterns;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RecipientPattern {
    private CategoryPattern code;
    private String value;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Limit implements Serializable {
    private BigDecimal amount;
    private Boolean isExceeded;
    private BigDecimal balance;
  }
}

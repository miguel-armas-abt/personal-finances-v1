package com.demo.service.expenses.categories.dto.request;

import com.demo.service.commons.enums.Currency;
import com.demo.service.expenses.categories.enums.CategoryPattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ExpenseCategoryRequestDto implements Serializable {

  private Currency currency;

  @Valid
  private List<Category> categories;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Category {
    @NotBlank
    private String name;

    @Positive
    private BigDecimal limit;

    @Valid
    private List<RecipientPattern> recipientPatterns;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RecipientPattern {

    @NotNull
    private CategoryPattern code;

    @NotBlank
    private String value;
  }
}

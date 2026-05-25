package com.demo.service.expenses.crud.dto.request;

import com.demo.service.commons.constants.Regex;
import com.demo.service.commons.enums.Currency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseUpdateRequestDto implements Serializable {

  @NotBlank
  @Pattern(regexp = Regex.DATE_ISO_8601)
  private String date;

  @Valid
  @NotNull
  private ExpenseDetail detail;

  private Currency currency;

  @NotNull
  @Positive
  private BigDecimal amount;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseDetail {
    @NotBlank
    private String category;

    private String comments;

    @NotBlank
    private String recipient;
  }
}

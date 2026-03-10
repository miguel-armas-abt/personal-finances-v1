package io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request;

import io.github.miguelarmasabt.commons.constants.Regex;
import io.github.miguelarmasabt.commons.enums.Currency;
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
public class ExpenseSaveRequestDto implements Serializable {

  private String gmailMessageId;

  @NotBlank
  @Pattern(regexp = Regex.DATE_ISO_8601, message = "{date.pattern}")
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
    private String category;

    private String comments;

    private String recipient;

    @NotBlank
    private String source;
  }
}

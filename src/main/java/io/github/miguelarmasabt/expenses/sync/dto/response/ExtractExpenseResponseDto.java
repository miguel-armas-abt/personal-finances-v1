package io.github.miguelarmasabt.expenses.sync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExtractExpenseResponseDto implements Serializable {

  private String gmailMessageId;
  private Instant gmailMessageReceivedAt;
  private Instant date;
  private ExpenseDetail detail;
  private String currency;
  private BigDecimal amount;

  @Builder
  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseDetail {
    private String category;
    private String recipient;
    private String source;
  }
}
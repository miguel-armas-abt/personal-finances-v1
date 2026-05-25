package com.demo.service.expenses.crud.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDto implements Serializable {

  private String id;

  @JsonIgnore
  private String gmailMessageId;

  private ExpenseDate date;
  private ExpenseDetail detail;
  private Currency currency;
  private BigDecimal amount;

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
  public static class ExpenseDate {
    private Instant utc;
    private String formatted;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseDetail {
    private String category;
    private String comments;
    private String recipient;
    private String source;
  }
}

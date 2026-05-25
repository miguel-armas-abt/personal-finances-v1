package com.demo.service.expenses.csv.dto;

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
public class ExpenseCsvRowDto implements Serializable {

  private String gmailMessageId;
  private String date;
  private String source;
  private String currency;
  private BigDecimal amount;
  private String category;
  private String comments;
  private String recipient;
}
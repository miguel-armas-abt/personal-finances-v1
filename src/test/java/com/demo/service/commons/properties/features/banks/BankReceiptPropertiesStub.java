package com.demo.service.commons.properties.features.banks;

import lombok.Data;

@Data
public class BankReceiptPropertiesStub implements BankReceiptProperties {

  private String from;
  private String subject;
  private String amountAndCurrencyRegex;
  private String recipientRegex;
  private String dateRegex;

  @Override
  public String from() {
    return this.from;
  }

  @Override
  public String subject() {
    return this.subject;
  }
}
package com.demo.service.commons.properties.features;

import com.demo.service.commons.properties.features.banks.BankReceiptProperties;
import com.demo.service.commons.properties.features.banks.BankReceiptPropertiesStub;
import com.demo.service.commons.properties.features.csv.export.CsvProperties;
import com.demo.service.commons.properties.features.gmail.GmailMessageContentProperties;
import com.demo.service.commons.properties.features.gmail.GmailMessageProperties;
import com.demo.service.commons.properties.features.search.criteria.SearchCriteriaProperties;
import lombok.Data;

import java.util.Map;

@Data
public class FeaturePropertiesStub implements FeatureProperties {

  private Map<String, BankReceiptPropertiesStub> bankReceipts;

  @Override
  public SearchCriteriaProperties searchCriteria() {
    return null;
  }

  @Override
  public Map<String, BankReceiptProperties> bankReceipts() {
    return (Map) bankReceipts;
  }

  @Override
  public CsvProperties csv() {
    return null;
  }

  @Override
  public GmailMessageProperties gmailMessages() {
    return null;
  }

  @Override
  public GmailMessageContentProperties gmailMessageContent() {
    return null;
  }
}
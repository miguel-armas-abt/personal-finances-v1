package com.demo.service.commons.properties.features;

import com.demo.service.commons.properties.features.banks.BankReceiptProperties;
import com.demo.service.commons.properties.features.csv.export.CsvProperties;
import com.demo.service.commons.properties.features.gmail.GmailMessageContentProperties;
import com.demo.service.commons.properties.features.gmail.GmailMessageProperties;
import com.demo.service.commons.properties.features.search.criteria.SearchCriteriaProperties;

import java.util.Map;

public interface FeatureProperties {

  SearchCriteriaProperties searchCriteria();

  Map<String, BankReceiptProperties> bankReceipts();

  CsvProperties csv();

  GmailMessageProperties gmailMessages();

  GmailMessageContentProperties gmailMessageContent();
}

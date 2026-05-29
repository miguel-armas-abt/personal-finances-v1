package io.github.miguelarmasabt.commons.properties.features;

import io.github.miguelarmasabt.commons.properties.features.banks.BankReceiptProperties;
import io.github.miguelarmasabt.commons.properties.features.csv.export.CsvProperties;
import io.github.miguelarmasabt.commons.properties.features.gmail.GmailMessageContentProperties;
import io.github.miguelarmasabt.commons.properties.features.gmail.GmailMessageProperties;
import io.github.miguelarmasabt.commons.properties.features.search.criteria.SearchCriteriaProperties;

import java.util.Map;

public interface FeatureProperties {

  SearchCriteriaProperties searchCriteria();

  Map<String, BankReceiptProperties> bankReceipts();

  CsvProperties csv();

  GmailMessageProperties gmailMessages();

  GmailMessageContentProperties gmailMessageContent();
}

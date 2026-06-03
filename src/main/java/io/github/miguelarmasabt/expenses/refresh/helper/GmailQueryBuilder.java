package io.github.miguelarmasabt.expenses.refresh.helper;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.utils.DateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class GmailQueryBuilder {

  private final ApplicationProperties properties;

  /**
   * @param checkpointAt is in yyyy/MM/dd format.
   */
  public String buildBankReceiptQueryAfter(String checkpointAt) {
    DateUtil.validateGmailDateYyyyMmDd(checkpointAt);
    String conditions = properties.features().expenses().bankReceipts()
        .values()
        .stream()
        .map(receipt -> "(from:" + receipt.from() + " subject:\"" + receipt.subject() + "\")")
        .collect(Collectors.joining(" OR "));

    return "(" + conditions + ") after:" + checkpointAt;
  }
}

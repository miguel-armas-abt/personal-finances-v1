package com.demo.service.finances.expenses.extracted.helper;

import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.commons.utils.DateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class GmailParameterHelper {

  private final ApplicationProperties properties;

  /**
   * @param lastSeenAt is in yyyy/MM/dd format.
   */
  public String getGmailQuery(String lastSeenAt) {
    DateUtil.validateGmailDateYyyyMmDd(lastSeenAt);
    String conditions = properties.features().bankReceipts()
        .values()
        .stream()
        .map(receipt -> "(from:" + receipt.from() + " subject:\"" + receipt.subject() + "\")")
        .collect(Collectors.joining(" OR "));

    return "(" + conditions + ") after:" + lastSeenAt;
  }

  public Long getPageSize() {
    return properties.features().gmailMessages().pageSize();
  }
}

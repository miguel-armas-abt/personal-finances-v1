package io.github.miguelarmasabt.expenses.extracted.helper;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.utils.DateUtil;
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
  public String buildGmailQuery(String lastSeenAt) {
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

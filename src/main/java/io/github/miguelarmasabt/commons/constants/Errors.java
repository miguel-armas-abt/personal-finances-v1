package io.github.miguelarmasabt.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Errors {

  public static String UNSUPPORTED_CURRENCY = "0001";
  public static String INVALID_CSV_ROW = "0008";
  public static String MALFORMED_CSV = "0009";
  public static String REQUIRED_USER_CODE = "0003";
  public static String NO_SUCH_HEADER_FROM = "0015";
  public static String UNSUPPORTED_EXTRACT_EXPENSE_STRATEGY = "0016";

}

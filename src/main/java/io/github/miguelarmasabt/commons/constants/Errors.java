package io.github.miguelarmasabt.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Errors {

  public static String UNSUPPORTED_CURRENCY = "0001";
  public static String ENCODING_CSV_ERROR = "0008";
  public static String MALFORMED_CSV = "0009";
  public static String REQUIRED_USER_CODE = "0003";
  public static String NO_SUCH_HEADER_FROM = "0015";
  public static String UNSUPPORTED_EXTRACT_EXPENSE_STRATEGY = "0016";
  public static String NULL_CSV_FILE = "0013";
  public static String INVALID_CSV_ROW_MSG01 = "0012";
  public static String INVALID_CSV_ROW_MSG02 = "0014";
  public static String INVALID_CSV_HEADER_MSG01 = "0011";
  public static String INVALID_CSV_HEADER_MSG02 = "0017";
}

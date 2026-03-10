package io.github.miguelarmasabt.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Errors {

  /**
   * commons
   */
  public static String INVALID_OBJECT_ID = "1000";
  public static String INVALID_DATE = "1001";
  public static String UNSUPPORTED_CURRENCY = "1002";
  public static String UNSUPPORTED_CURRENCY_SYMBOL = "1003";
  public static String REQUIRED_USER_CODE = "1004";

  /**
   * expenses.csv
   */
  public static String ENCODING_CSV_ERROR = "2000";
  public static String MALFORMED_CSV = "2001";
  public static String INVALID_CSV_EXTENSION = "2002";
  public static String NULL_CSV_FILE = "2003";
  public static String UNEXPECTED_NUMBER_OF_CSV_HEADERS = "2004";
  public static String INVALID_CSV_ROW_MSG02 = "2005";
  public static String INVALID_CSV_HEADER_MSG01 = "2006";
  public static String INVALID_CSV_HEADER_MSG02 = "2007";

  /**
   * expenses.bank-receipts
   */
  public static String NO_SUCH_HEADER_FROM = "3000";
  public static String UNSUPPORTED_EXTRACT_EXPENSE_STRATEGY = "3001";

  /**
   * expenses.categories
   */
  public static String UNSUPPORTED_CATEGORY_PATTERN = "4000";

  /**
   * expenses.crud
   */
  public static String EXPENSE_NOT_FOUND = "4001";
}

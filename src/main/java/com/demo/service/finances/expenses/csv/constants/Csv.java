package com.demo.service.finances.expenses.csv.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Csv {

  public static final String CSV_EXTENSION = ".csv";
  public static final char DELIMITER = ',';
  public static final char QUOTE = '"';
  public static final String LINE_SEPARATOR = "\n";
  public static final String CARRIAGE_RETURN = "\r";

  public static final String[] EXPECTED_HEADERS = {
      "gmailMessageId",
      "date",
      "source",
      "currency",
      "amount",
      "category",
      "comments",
      "recipient"
  };
}

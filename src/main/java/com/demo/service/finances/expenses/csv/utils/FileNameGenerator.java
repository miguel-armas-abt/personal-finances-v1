package com.demo.service.finances.expenses.csv.utils;

import com.demo.service.commons.utils.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameGenerator {

  private static final String FILE_PREFIX = "expenses";
  private static final String FILE_EXTENSION = ".csv";

  public static String generateFileName() {
    String timestamp = ZonedDateTime.now(DateUtil.LIMA_ZONE_ID).format(DateUtil.BASIC_DATE_FORMATTER);
    return FILE_PREFIX + "-" + timestamp + FILE_EXTENSION;
  }
}

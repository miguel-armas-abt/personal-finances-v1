package com.demo.service.finances.expenses.csv.helper;

import com.demo.service.commons.exceptions.UserCodeRequiredException;
import com.demo.service.finances.expenses.csv.constants.Csv;
import com.demo.service.finances.expenses.csv.exceptions.InvalidCsvExtensionException;
import com.demo.service.finances.expenses.csv.exceptions.InvalidCsvHeaderException;
import com.demo.service.finances.expenses.csv.exceptions.InvalidCsvRowException;
import com.demo.service.finances.expenses.csv.exceptions.NullCsvFileException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.math.BigDecimal;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportExpenseCsvValidator {

  public static void validateImportFile(String userCode, FileUpload file) {
    if (Objects.isNull(userCode) || userCode.isBlank()) {
      throw new UserCodeRequiredException();
    }

    if (Objects.isNull(file) || Objects.isNull(file.uploadedFile())) {
      throw new NullCsvFileException();
    }

    String fileName = Objects.requireNonNullElse(file.fileName(), "");
    if (!fileName.toLowerCase().endsWith(Csv.CSV_EXTENSION)) {
      throw new InvalidCsvExtensionException();
    }
  }

  public static void validateHeaders(String[] headers) {
    if (Objects.isNull(headers) || headers.length != Csv.EXPECTED_HEADERS.length) {
      throw new InvalidCsvHeaderException();
    }

    for (int index = 0; index < Csv.EXPECTED_HEADERS.length; index++) {
      String expectedHeader = Csv.EXPECTED_HEADERS[index];
      String actualHeader = headers[index];

      if (!expectedHeader.equals(actualHeader)) {
        throw new InvalidCsvHeaderException(index, expectedHeader, actualHeader);
      }
    }
  }

  public static String validateRequiredAndGet(String value, String field, long rowNumber) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new InvalidCsvRowException(rowNumber, field);
    }
    return value.trim();
  }

  public static BigDecimal validateRequiredBigDecimalAndGet(String value, String field, long rowNumber) {
    try {
      String number = validateRequiredAndGet(value, field, rowNumber);
      return new BigDecimal(number);
    } catch (NumberFormatException exception) {
      throw new InvalidCsvRowException(rowNumber, field);
    }
  }
}

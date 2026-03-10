package com.demo.service.finances.expenses.csv.helper;

import com.demo.service.finances.expenses.csv.constants.Csv;
import com.demo.service.finances.expenses.csv.dto.ExpenseCsvRowDto;
import com.demo.service.finances.expenses.csv.exceptions.CsvEncodingException;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ExportExpenseCsvEncoder {

  private static final int MIN_ENCODED_ROWS_CAPACITY = 256;
  private static final int ESTIMATED_ROW_LENGTH = 96;
  private static final int ESTIMATED_LINE_LENGTH = 128;

  public Buffer encodeHeader() {
    return toBuffer(encodeLine(Csv.EXPECTED_HEADERS));
  }

  public Buffer encodeRows(List<ExpenseCsvRowDto> rows) {
    try {
      StringBuilder builder = new StringBuilder(Math.max(MIN_ENCODED_ROWS_CAPACITY, rows.size() * ESTIMATED_ROW_LENGTH));

      for (ExpenseCsvRowDto row : rows) {
        builder.append(encodeRow(row));
      }

      return toBuffer(builder.toString());
    } catch (RuntimeException exception) {
      throw new CsvEncodingException(exception);
    }
  }

  private String encodeRow(ExpenseCsvRowDto row) {
    return encodeLine(
        emptyIfNull(row.getGmailMessageId()),
        emptyIfNull(row.getDate()),
        emptyIfNull(row.getSource()),
        emptyIfNull(row.getCurrency()),
        toPlainStringOrEmpty(row.getAmount()),
        emptyIfNull(row.getCategory()),
        emptyIfNull(row.getComments()),
        emptyIfNull(row.getRecipient())
    );
  }

  private String encodeLine(String... values) {
    StringBuilder builder = new StringBuilder(ESTIMATED_LINE_LENGTH);

    for (int index = 0; index < values.length; index++) {
      if (index > 0) {
        builder.append(Csv.DELIMITER);
      }
      builder.append(escape(values[index]));
    }

    builder.append(Csv.LINE_SEPARATOR);
    return builder.toString();
  }

  private String escape(String value) {
    return Optional.ofNullable(value)
        .map(this::escapeIfNeeded)
        .orElse(StringUtils.EMPTY);
  }

  private String escapeIfNeeded(String value) {
    return mustQuote(value)
        ? quote(escapeQuotes(value))
        : value;
  }

  private String escapeQuotes(String value) {
    return value.replace(
        String.valueOf(Csv.QUOTE),
        String.valueOf(Csv.QUOTE) + Csv.QUOTE
    );
  }

  private String quote(String value) {
    return Csv.QUOTE + value + Csv.QUOTE;
  }

  private boolean mustQuote(String value) {
    return value.indexOf(Csv.DELIMITER) >= 0
        || value.indexOf(Csv.QUOTE) >= 0
        || value.contains(Csv.LINE_SEPARATOR)
        || value.contains(Csv.CARRIAGE_RETURN);
  }

  private Buffer toBuffer(String value) {
    return Buffer.buffer(value, StandardCharsets.UTF_8.name());
  }

  private String emptyIfNull(String value) {
    return Optional.ofNullable(value)
        .orElse(StringUtils.EMPTY);
  }

  private String toPlainStringOrEmpty(BigDecimal value) {
    return Optional.ofNullable(value)
        .map(BigDecimal::toPlainString)
        .orElse(StringUtils.EMPTY);
  }
}
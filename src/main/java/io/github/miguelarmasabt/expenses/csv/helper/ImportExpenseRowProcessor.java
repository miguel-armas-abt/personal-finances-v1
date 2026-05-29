package io.github.miguelarmasabt.expenses.csv.helper;

import io.github.miguelarmasabt.expenses.csv.constants.Csv;
import io.github.miguelarmasabt.expenses.csv.dto.ExpenseCsvRowDto;
import io.github.miguelarmasabt.expenses.csv.exceptions.InvalidCsvRowException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ImportExpenseRowProcessor implements RowProcessor {

  private final List<ExpenseCsvRowDto> rows = new ArrayList<>();
  private String[] headers;

  private static String normalizeOptional(String value) {
    return Optional.ofNullable(value)
        .filter(x -> !x.isBlank())
        .map(String::trim)
        .orElse(null);
  }

  @Override
  public void processStarted(ParsingContext context) {
    this.headers = context.headers();
  }

  @Override
  public void rowProcessed(String[] row, ParsingContext context) {
    long currentRowNumber = context.currentLine();

    if (Objects.isNull(row) || row.length != Csv.EXPECTED_HEADERS.length) {
      throw new InvalidCsvRowException(currentRowNumber, Csv.EXPECTED_HEADERS.length);
    }

    rows.add(new ExpenseCsvRowDto(
        normalizeOptional(row[0]),
        ImportExpenseCsvValidator.validateRequiredAndGet(row[1], "date", currentRowNumber),
        ImportExpenseCsvValidator.validateRequiredAndGet(row[2], "source", currentRowNumber),
        ImportExpenseCsvValidator.validateRequiredAndGet(row[3], "currency", currentRowNumber),
        ImportExpenseCsvValidator.validateRequiredBigDecimalAndGet(row[4], "amount", currentRowNumber),
        normalizeOptional(row[5]),
        normalizeOptional(row[6]),
        normalizeOptional(row[7])
    ));
  }

  @Override
  public void processEnded(ParsingContext context) {
    // no-op
  }
}

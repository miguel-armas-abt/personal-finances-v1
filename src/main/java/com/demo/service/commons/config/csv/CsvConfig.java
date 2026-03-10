package com.demo.service.commons.config.csv;

import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvConfig {

  public static CsvParserSettings createParserSettings(RowProcessor rowProcessor) {
    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setLineSeparatorDetectionEnabled(true);
    settings.setSkipEmptyLines(true);
    settings.setIgnoreLeadingWhitespaces(false);
    settings.setIgnoreTrailingWhitespaces(false);
    settings.setProcessor(rowProcessor);

    return settings;
  }
}

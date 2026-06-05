package io.github.miguelarmasabt.expenses.bank.receipts.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankReceiptTextNormalizer {

  private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  public static String normalize(String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .map(text -> text.toLowerCase(Locale.ROOT))
        .map(text -> Normalizer.normalize(text, Normalizer.Form.NFD))
        .map(text -> DIACRITICS.matcher(text).replaceAll(StringUtils.EMPTY))
        .map(text -> MULTIPLE_SPACES.matcher(text).replaceAll(StringUtils.SPACE))
        .orElse(StringUtils.EMPTY);
  }
}

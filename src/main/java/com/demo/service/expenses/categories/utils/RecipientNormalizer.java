package com.demo.service.expenses.categories.utils;

import com.demo.commons.constants.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipientNormalizer {

  public static String normalize(String value) {
    return Optional.ofNullable(value)
        .map(v -> Normalizer.normalize(v, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", StringUtils.EMPTY)
            .replaceAll("\\s+", Strings.SPACE)
            .trim())
        .orElse(StringUtils.EMPTY);
  }
}

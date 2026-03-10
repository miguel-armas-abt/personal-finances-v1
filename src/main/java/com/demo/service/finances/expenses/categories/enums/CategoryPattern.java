package com.demo.service.finances.expenses.categories.enums;

import com.demo.service.finances.expenses.categories.exceptions.UnsupportedCategoryPatternException;
import com.demo.service.finances.expenses.categories.utils.RecipientNormalizer;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum CategoryPattern {

  SAME_AS(400) {
    @Override
    public Pattern toRegex(String value) {
      return Pattern.compile("^" + Pattern.quote(RecipientNormalizer.normalize(value)) + "$", FLAGS);
    }
  },
  START_WITH(300) {
    @Override
    public Pattern toRegex(String value) {
      return Pattern.compile("^" + Pattern.quote(RecipientNormalizer.normalize(value)) + ".*$", FLAGS);
    }
  },
  CONTAINS(200) {
    @Override
    public Pattern toRegex(String value) {
      return Pattern.compile("^.*" + Pattern.quote(RecipientNormalizer.normalize(value)) + ".*$", FLAGS);
    }
  },
  ENDS_WITH(300) {
    @Override
    public Pattern toRegex(String value) {
      return Pattern.compile("^.*" + Pattern.quote(RecipientNormalizer.normalize(value)) + "$", FLAGS);
    }
  };

  private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

  private final int priorityScore;

  CategoryPattern(int priorityScore) {
    this.priorityScore = priorityScore;
  }

  public abstract Pattern toRegex(String value);

  public int priorityScore() {
    return priorityScore;
  }

  public int score(String value) {
    return priorityScore + RecipientNormalizer.normalize(value).length();
  }

  public static CategoryPattern fromCode(String code) {
    return Arrays.stream(values())
        .filter(pattern -> pattern.name().equalsIgnoreCase(code))
        .findFirst()
        .orElseThrow(() -> new UnsupportedCategoryPatternException(code));
  }
}

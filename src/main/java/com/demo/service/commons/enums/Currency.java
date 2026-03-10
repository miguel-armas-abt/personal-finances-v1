package com.demo.service.commons.enums;

import com.demo.service.commons.exceptions.UnsupportedCurrencyCodeException;
import com.demo.service.commons.exceptions.UnsupportedCurrencySymbolException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Currency {

  PEN("S/"),
  USD("$"),
  UNKNOWN("unknown");

  private final String symbol;

  public static Currency parseFromSymbol(String symbol) {
    return Arrays.stream(Currency.values())
        .filter(currency -> currency.getSymbol().equalsIgnoreCase(symbol))
        .findFirst()
        .orElseThrow(() -> new UnsupportedCurrencySymbolException(symbol));
  }

  public static Currency parseFromCode(String code) {
    return Arrays.stream(Currency.values())
        .filter(currency -> currency.name().equalsIgnoreCase(code))
        .findFirst()
        .orElseThrow(() -> new UnsupportedCurrencyCodeException(code));
  }
}

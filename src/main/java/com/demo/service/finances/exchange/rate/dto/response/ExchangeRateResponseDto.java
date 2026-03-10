package com.demo.service.finances.exchange.rate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponseDto implements Serializable {

  private String date;
  private String base;
  private String quote;
  private BigDecimal rate;

}

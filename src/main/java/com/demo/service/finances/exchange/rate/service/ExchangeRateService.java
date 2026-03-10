package com.demo.service.finances.exchange.rate.service;

import com.demo.service.finances.exchange.rate.dto.response.ExchangeRateResponseDto;
import io.smallrye.mutiny.Uni;

public interface ExchangeRateService {

  Uni<ExchangeRateResponseDto> getExchangeRate(String base, String quote);
}

package com.demo.service.finances.exchange.rate.service.impl;

import com.demo.service.finances.exchange.rate.dto.response.ExchangeRateResponseDto;
import com.demo.service.finances.exchange.rate.mapper.ExchangeRateMapper;
import com.demo.service.finances.exchange.rate.repository.ExchangeRateRepository;
import com.demo.service.finances.exchange.rate.service.ExchangeRateService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private final ExchangeRateMapper mapper;

  @RestClient
  ExchangeRateRepository repository;

  @Override
  public Uni<ExchangeRateResponseDto> getExchangeRate(String base, String quote) {
    return repository.getExchangeRate(base, quote)
        .map(mapper::toDto);
  }
}

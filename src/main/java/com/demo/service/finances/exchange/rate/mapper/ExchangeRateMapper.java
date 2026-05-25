package com.demo.service.finances.exchange.rate.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.finances.exchange.rate.dto.response.ExchangeRateResponseDto;
import com.demo.service.finances.exchange.rate.repository.wrapper.response.ExchangeRateResponseWrapper;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface ExchangeRateMapper {

  ExchangeRateResponseDto toDto(ExchangeRateResponseWrapper exchangeRate);
}

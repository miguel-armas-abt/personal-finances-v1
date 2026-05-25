package com.demo.service.finances.extracted.expenses.strategy;

import com.demo.service.commons.properties.ApplicationProperties;
import com.demo.service.commons.properties.ApplicationPropertiesStub;
import com.demo.service.commons.repository.gmail.wrapper.response.MessageContentResponseWrapper;
import com.demo.service.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import com.demo.service.expenses.extracted.strategy.ExtractExpenseHelper;
import com.demo.service.expenses.extracted.strategy.ExtractExpenseStrategy;
import com.demo.service.expenses.extracted.strategy.ExtractExpenseStrategyDispatcher;
import com.demo.service.expenses.extracted.strategy.bbva.BBVADebitCardStrategy;
import com.demo.service.expenses.extracted.strategy.bbva.BBVAPlinMerchantQRStrategy;
import com.demo.service.expenses.extracted.strategy.bbva.BBVAPlinStrategy;
import com.demo.service.expenses.extracted.strategy.bbva.BBVAServicePaymentStrategy;
import com.demo.service.expenses.extracted.strategy.ibk.IBKPlinStrategy;
import com.demo.service.tools.InstanceStub;
import com.demo.service.tools.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.Instance;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractExpenseStrategyDispatcherTest {

  private static final JsonSerializer jsonSerializer = new JsonSerializer(new ObjectMapper());
  private ExtractExpenseStrategyDispatcher dispatcher;

  @BeforeEach
  void setup() {
    dispatcher = Stub.extractExpenseStrategyDispatcher();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "BBVA/BBVA_DEBIT_CARD.json",
      "BBVA/BBVA_PLIN.json",
      "BBVA/BBVA_PLIN_MERCHANT_QR.json",
      "BBVA/BBVA_SERVICE_PAYMENT.json",
      "IBK/IBK_PLIN.json",
  })
  @DisplayName("Given a message content, when select strategy and extract fields, then return DTO")
  void givenMessageContent_WhenSelectStrategyAndExtractFields_ThenReturnDto(String file) {
    // Arrange
    MessageContentResponseWrapper message = readJson(file);

    // Act
    ExtractExpenseResponseDto result = dispatcher.toDto(message)
        .await()
        .indefinitely();

    // Assert
    assertAll(
        () -> assertNotNull(result),
        () -> assertTrue(file.contains(result.getDetail().getSource())),
        () -> assertNotNull(result.getGmailMessageId()),
        () -> assertNotNull(result.getDate()),
        () -> assertNotNull(result.getCurrency()),
        () -> assertTrue(result.getAmount().compareTo(BigDecimal.ZERO) > 0),
        () -> assertNotNull(result.getDetail()),
        () -> assertNotNull(result.getDetail().getRecipient()),
        () -> assertFalse(result.getDetail().getRecipient().isBlank())
    );
  }

  private MessageContentResponseWrapper readJson(String fileName) {
    return jsonSerializer.readElementFromFile("/expenses/extracted/" + fileName, MessageContentResponseWrapper.class);
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class Stub {

    static ExtractExpenseStrategyDispatcher extractExpenseStrategyDispatcher() {
      return new ExtractExpenseStrategyDispatcher(extractExpenseHelper(), strategies());
    }

    static Instance<ExtractExpenseStrategy> strategies() {
      return InstanceStub.of(List.of(
          new BBVAPlinStrategy(),
          new BBVAPlinMerchantQRStrategy(),
          new BBVADebitCardStrategy(),
          new BBVAServicePaymentStrategy(),
          new IBKPlinStrategy()
      ));
    }

    static ExtractExpenseHelper extractExpenseHelper() {
      return new ExtractExpenseHelper(applicationProperties());
    }

    static ApplicationProperties applicationProperties() {
      return jsonSerializer.readElementFromFile("/expenses/extracted/ApplicationProperties.json", ApplicationPropertiesStub.class);
    }

  }
}
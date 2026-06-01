package io.github.miguelarmasabt.expenses.extracted.strategy;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.properties.ApplicationPropertiesStub;
import io.github.miguelarmasabt.commons.repository.gmail.wrapper.response.MessageContentResponseWrapper;
import io.github.miguelarmasabt.expenses.extracted.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.extracted.strategy.bbva.BBVADebitCardExtractorStrategy;
import io.github.miguelarmasabt.expenses.extracted.strategy.bbva.BBVAPlinMerchantQRExtractorStrategy;
import io.github.miguelarmasabt.expenses.extracted.strategy.bbva.BBVAPlinExtractorStrategy;
import io.github.miguelarmasabt.expenses.extracted.strategy.bbva.BBVAServicePaymentExtractorStrategy;
import io.github.miguelarmasabt.expenses.extracted.strategy.ibk.IBKPlinExtractorStrategy;
import io.github.miguelarmasabt.tools.InstanceStub;
import io.github.miguelarmasabt.tools.JsonSerializer;
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

class BankReceiptExpenseExtractorStrategyDispatcherTest {

  private static final JsonSerializer jsonSerializer = new JsonSerializer(new ObjectMapper());
  private BankReceiptExpenseExtractorDispatcher dispatcher;

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

    static BankReceiptExpenseExtractorDispatcher extractExpenseStrategyDispatcher() {
      return new BankReceiptExpenseExtractorDispatcher(extractExpenseHelper(), strategies());
    }

    static Instance<BankReceiptExpenseExtractorStrategy> strategies() {
      return InstanceStub.of(List.of(
          new BBVAPlinExtractorStrategy(),
          new BBVAPlinMerchantQRExtractorStrategy(),
          new BBVADebitCardExtractorStrategy(),
          new BBVAServicePaymentExtractorStrategy(),
          new IBKPlinExtractorStrategy()
      ));
    }

    static ExpenseExtractorHelper extractExpenseHelper() {
      return new ExpenseExtractorHelper(applicationProperties());
    }

    static ApplicationProperties applicationProperties() {
      return jsonSerializer.readElementFromFile("/expenses/extracted/ApplicationProperties.json", ApplicationPropertiesStub.class);
    }

  }
}
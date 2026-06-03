package io.github.miguelarmasabt.expenses.refresh.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.properties.features.FeatureProperties;
import io.github.miguelarmasabt.commons.properties.features.expenses.ExpenseProperties;
import io.github.miguelarmasabt.expenses.refresh.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.expenses.refresh.strategy.bbva.BBVADebitCardExtractorStrategy;
import io.github.miguelarmasabt.expenses.refresh.strategy.bbva.BBVAPlinExtractorStrategy;
import io.github.miguelarmasabt.expenses.refresh.strategy.bbva.BBVAPlinMerchantQRExtractorStrategy;
import io.github.miguelarmasabt.expenses.refresh.strategy.bbva.BBVAServicePaymentExtractorStrategy;
import io.github.miguelarmasabt.expenses.refresh.strategy.bcp.BCPDebitCardExtractorStrategy;
import io.github.miguelarmasabt.expenses.refresh.strategy.ibk.IBKPlinExtractorStrategy;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.github.miguelarmasabt.tools.InstanceStub;
import io.github.miguelarmasabt.tools.JsonSerializer;
import jakarta.enterprise.inject.Instance;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
      "BCP/BCP_DEBIT_CARD.json",
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
          new IBKPlinExtractorStrategy(),
          new BCPDebitCardExtractorStrategy()
      ));
    }

    static ExpenseExtractorHelper extractExpenseHelper() {
      return new ExpenseExtractorHelper(applicationProperties());
    }

    static ApplicationProperties applicationProperties() {
      ExpenseProperties expenseProperties = Mockito.mock(ExpenseProperties.class);
      FeatureProperties featureProperties = Mockito.mock(FeatureProperties.class);
      ApplicationProperties properties = Mockito.mock(ApplicationProperties.class);

      ExpensePropertiesStub expensePropertiesStub = jsonSerializer.readElementFromFile(
          "/expenses/extracted/ExpensePropertiesStub.json",
          ExpensePropertiesStub.class);

      when(expenseProperties.bankReceipts()).thenReturn(expensePropertiesStub.getBankReceipts());
      when(featureProperties.expenses()).thenReturn(expenseProperties);
      when(properties.features()).thenReturn(featureProperties);
      return properties;
    }

  }

  @Data
  public static class ExpensePropertiesStub {
    @JsonDeserialize(contentAs = BankReceiptPropertiesStub.class)
    private Map<String, ExpenseProperties.BankReceipt> bankReceipts;
  }

  @Data
  public static class BankReceiptPropertiesStub implements ExpenseProperties.BankReceipt {

    private String from;
    private String subject;

    @Override
    public String from() {
      return this.from;
    }

    @Override
    public String subject() {
      return this.subject;
    }
  }
}
package io.github.miguelarmasabt.expenses.bank.receipts;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.miguelarmasabt.expenses.bank.receipts.helper.BankReceiptExpenseExtractorDispatcher;
import io.github.miguelarmasabt.expenses.bank.receipts.repository.BankReceiptTemplateRepository;
import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.github.miguelarmasabt.expenses.sync.dto.response.ExtractExpenseResponseDto;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.github.miguelarmasabt.tools.JsonSerializer;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankReceiptTemplateExtractionTest {

  private static final String SEED_FILE = "/expenses/bank-receipts/bank-receipt-templates.seed.json";
  private static final String GMAIL_MESSAGE_BASE_PATH = "/expenses/bank-receipts/";

  private static final JsonSerializer jsonSerializer = new JsonSerializer(new ObjectMapper());

  @Inject
  BankReceiptTemplateRepository templateRepository;

  @Inject
  BankReceiptExpenseExtractorDispatcher dispatcher;

  @BeforeAll
  void setup() {
    templateRepository.deleteAll()
        .await()
        .indefinitely();

    BankReceiptTemplateSeed seed = jsonSerializer.readElementFromFile(
        SEED_FILE,
        BankReceiptTemplateSeed.class
    );

    List<BankReceiptTemplateEntity> templates = BankReceiptTemplateSeedResolver.resolve(seed);

    templateRepository.persist(templates)
        .await()
        .indefinitely();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("bankReceiptCases")
  @DisplayName("Given bank receipt templates stored in MongoDB, when extracting Gmail receipt, then return expense DTO")
  void givenBankReceiptTemplatesStoredInMongo_WhenExtractReceipt_ThenReturnExpenseDto(String expectedSource,
                                                                                      String file) {
    MessageContentResponseWrapper message = readMessage(file);

    List<BankReceiptTemplateEntity> templates = templateRepository.findEnabled()
        .await()
        .indefinitely();

    ExtractExpenseResponseDto result = dispatcher.toDto(message, templates)
        .await()
        .indefinitely();

    assertAll(
        () -> assertNotNull(result),
        () -> assertNotNull(result.getDetail()),
        () -> assertEquals(expectedSource, result.getDetail().getSource()),
        () -> assertNotNull(result.getGmailMessageId()),
        () -> assertNotNull(result.getGmailMessageReceivedAt()),
        () -> assertNotNull(result.getDate()),
        () -> assertNotNull(result.getCurrency()),
        () -> assertTrue(result.getAmount().compareTo(BigDecimal.ZERO) > 0),
        () -> assertNotNull(result.getDetail().getRecipient()),
        () -> assertFalse(result.getDetail().getRecipient().isBlank())
    );
  }

  @ParameterizedTest(name = "[{index}] template exists for {0}")
  @MethodSource("expectedTemplateCodes")
  @DisplayName("Given seed data, when loading templates from MongoDB, then all required templates exist")
  void givenSeedData_WhenLoadingTemplatesFromMongo_ThenTemplateExists(String expectedCode) {
    List<BankReceiptTemplateEntity> templates = templateRepository.findEnabled()
        .await()
        .indefinitely();

    Set<String> codes = templates.stream()
        .filter(Objects::nonNull)
        .map(BankReceiptTemplateEntity::getCode)
        .collect(Collectors.toSet());

    assertTrue(codes.contains(expectedCode), "Missing template: " + expectedCode);
  }

  private MessageContentResponseWrapper readMessage(String file) {
    return jsonSerializer.readElementFromFile(
        GMAIL_MESSAGE_BASE_PATH + file,
        MessageContentResponseWrapper.class
    );
  }

  private static Stream<Arguments> bankReceiptCases() {
    return Stream.of(
        Arguments.of("BBVA_DEBIT_CARD", "BBVA/BBVA_DEBIT_CARD.json"),
        Arguments.of("BBVA_PLIN", "BBVA/BBVA_PLIN.json"),
        Arguments.of("BBVA_PLIN_MERCHANT_QR", "BBVA/BBVA_PLIN_MERCHANT_QR.json"),
        Arguments.of("BBVA_SERVICE_PAYMENT", "BBVA/BBVA_SERVICE_PAYMENT.json"),
        Arguments.of("IBK_PLIN", "IBK/IBK_PLIN.json"),
        Arguments.of("BCP_DEBIT_CARD", "BCP/BCP_DEBIT_CARD.json")
    );
  }

  private static Stream<Arguments> expectedTemplateCodes() {
    return bankReceiptCases()
        .map(arguments -> Arguments.of(arguments.get()[0]));
  }
}
package io.github.miguelarmasabt.expenses.bank.receipts.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "bank_receipt_templates")
public class BankReceiptTemplateEntity {

  @BsonId
  private ObjectId id;

  private String code;
  private Boolean enabled;
  private String description;

  private Gmail gmail;
  private Extraction extraction;

  private Instant createdAt;
  private Instant updatedAt;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Gmail {
    private String from;
    private List<String> subjects;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Extraction {
    private RegexField recipient;
    private AmountCurrency amountCurrency;
    private DateField date;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RegexField {
    private String regex;
    private Integer group;
    private String defaultValue;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AmountCurrency {
    private String regex;
    private Integer amountGroup;
    private Integer currencyGroup;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DateField {
    private String regex;
    private Integer group;

    /**
     * Supported values:
     * LOCAL_DATE_TIME
     * LOCAL_DATE
     * SPLIT_DATE_TIME
     */
    private String mode;

    private String pattern;
    private String locale;
    private String zoneId;

    private Integer dateGroup;
    private Integer timeGroup;
    private String dateTimeSeparator;

    private Map<String, String> replacements;

    /**
     * Useful field for identifying the "replacements" dictionary during testing and script creation for mongosh.
     */
    private String replacementsRef;
  }
}

package com.demo.service.finances.expenses.categories.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "expense_categories")
public class ExpenseCategoryEntity {

  @BsonId
  private String userCode;

  private String currency;

  private List<Category> categories;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Category {
    private String name;
    private BigDecimal limit;
    private List<RecipientPattern> recipientPatterns;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RecipientPattern {
    private String code;
    private String value;
  }
}

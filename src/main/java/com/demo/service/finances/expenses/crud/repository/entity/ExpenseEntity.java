package com.demo.service.finances.expenses.crud.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "expenses")
public class ExpenseEntity {

  @BsonId
  private ObjectId id;

  private String userCode;
  private String gmailMessageId;
  private Instant date;
  private ExpenseDetail detail;
  private String currency;
  private BigDecimal amount;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseDetail {
    private String category;
    private String comments;
    private String recipient;
    private String source;
  }
}

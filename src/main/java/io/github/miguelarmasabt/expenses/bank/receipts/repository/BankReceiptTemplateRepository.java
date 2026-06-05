package io.github.miguelarmasabt.expenses.bank.receipts.repository;

import io.github.miguelarmasabt.expenses.bank.receipts.repository.entity.BankReceiptTemplateEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class BankReceiptTemplateRepository
    implements ReactivePanacheMongoRepositoryBase<BankReceiptTemplateEntity, ObjectId> {

  public Uni<List<BankReceiptTemplateEntity>> findEnabled() {
    return find("enabled", true)
        .list();
  }

  public Uni<BankReceiptTemplateEntity> findByCode(String code) {
    if (Objects.isNull(code) || code.isBlank()) {
      return Uni.createFrom().nullItem();
    }

    return find("code", code)
        .firstResult();
  }
}

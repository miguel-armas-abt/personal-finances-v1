package com.demo.service.finances.expenses.categories.repository;

import com.demo.service.finances.expenses.categories.repository.entity.ExpenseCategoryEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;

@ApplicationScoped
public class ExpenseCategoryRepository implements ReactivePanacheMongoRepositoryBase<ExpenseCategoryEntity, String> {

  public Uni<Void> updateByUserCode(ExpenseCategoryEntity category) {
    return findById(category.getUserCode())
        .onItem().ifNotNull().transformToUni(existing -> {
          if (Objects.nonNull(category.getCurrency())) {
            existing.setCurrency(category.getCurrency());
          }

          if (Objects.nonNull(category.getCategories()) && !category.getCategories().isEmpty()) {
            existing.setCategories(category.getCategories());
          }

          return persistOrUpdate(existing);
        })
        .onItem().ifNull().switchTo(() -> persist(category))
        .replaceWithVoid();
  }
}

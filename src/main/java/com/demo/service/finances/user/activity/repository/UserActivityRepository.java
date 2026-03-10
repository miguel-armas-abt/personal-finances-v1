package com.demo.service.finances.user.activity.repository;

import com.demo.service.finances.user.activity.repository.entity.UserActivityEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

@ApplicationScoped
public class UserActivityRepository implements ReactivePanacheMongoRepositoryBase<UserActivityEntity, String> {

  public Uni<Void> updateByUserCode(UserActivityEntity userActivity) {
    return findById(userActivity.getUserCode())
        .onItem().ifNotNull().transformToUni(existing -> {
          existing.setLastSeenAt(userActivity.getLastSeenAt());
          return persistOrUpdate(existing);
        })
        .onItem().ifNull().switchTo(() -> persist(userActivity))
        .replaceWithVoid();
  }

  public Uni<UserActivityEntity> findOrPersist(String userCode) {
    return findById(userCode)
        .onItem().ifNotNull().transformToUni(userActivity -> Uni.createFrom().item(userActivity))
        .onItem().ifNull().switchTo(() -> {
          UserActivityEntity userActivity = new UserActivityEntity();
          userActivity.setUserCode(userCode);
          userActivity.setLastSeenAt(Instant.now());
          return persist(userActivity);
        });
  }

}

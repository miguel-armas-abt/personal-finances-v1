package io.github.miguelarmasabt.commons.repository.user.activity;

import io.github.miguelarmasabt.commons.repository.user.activity.entity.UserActivityEntity;
import io.github.miguelarmasabt.commons.repository.user.activity.mapper.UserActivityMapper;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
public class UserActivityRepository implements ReactivePanacheMongoRepositoryBase<UserActivityEntity, String> {

  private final UserActivityMapper mapper;

  public Uni<Void> updateByUserCode(String userCode, Instant instant) {
    UserActivityEntity userActivity = mapper.toEntity(userCode, instant);
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

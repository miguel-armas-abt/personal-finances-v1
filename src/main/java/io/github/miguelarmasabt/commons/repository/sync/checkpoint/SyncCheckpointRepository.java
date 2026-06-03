package io.github.miguelarmasabt.commons.repository.sync.checkpoint;

import io.github.miguelarmasabt.commons.repository.sync.checkpoint.entity.SyncCheckpointEntity;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.mapper.SyncCheckpointMapper;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
public class SyncCheckpointRepository implements ReactivePanacheMongoRepositoryBase<SyncCheckpointEntity, ObjectId> {

  private final SyncCheckpointMapper mapper;

  public Uni<Void> updateByUserCode(String userCode,
                                    SyncScope scope,
                                    Instant checkpointAt) {
    return findByUserCodeAndScope(userCode, scope)
        .onItem().ifNotNull().transformToUni(existing -> {
          existing.setCheckpointAt(checkpointAt);
          return persistOrUpdate(existing);
        })
        .onItem().ifNull().switchTo(() -> {
          SyncCheckpointEntity entity = mapper.toEntity(userCode, scope, checkpointAt);
          return persist(entity);
        })
        .replaceWithVoid();
  }

  public Uni<SyncCheckpointEntity> findOrPersist(String userCode,
                                                 SyncScope scope) {
    return findByUserCodeAndScope(userCode, scope)
        .onItem().ifNotNull().transformToUni(checkpoint -> Uni.createFrom().item(checkpoint))
        .onItem().ifNull().switchTo(() -> {
          SyncCheckpointEntity entity = mapper.toEntity(userCode, scope, Instant.now());
          return persist(entity);
        });
  }

  private Uni<SyncCheckpointEntity> findByUserCodeAndScope(String userCode,
                                                           SyncScope scope) {
    return find("userCode = ?1 and scope = ?2", userCode, scope.name())
        .firstResult();
  }


}

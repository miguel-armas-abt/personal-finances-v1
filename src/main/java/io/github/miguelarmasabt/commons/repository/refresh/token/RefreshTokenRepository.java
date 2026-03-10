package io.github.miguelarmasabt.commons.repository.refresh.token;

import io.github.miguelarmasabt.commons.repository.refresh.token.entity.RefreshTokenEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class RefreshTokenRepository implements ReactivePanacheMongoRepositoryBase<RefreshTokenEntity, ObjectId> {

  public Uni<Optional<RefreshTokenEntity>> findByTokenHash(String tokenHash) {
    return find("tokenHash", tokenHash).firstResultOptional();
  }

  public Uni<Long> revokeFamily(String familyId, Instant revokedAt) {
    return update("revokedAt", revokedAt)
        .where("familyId = ?1 and revokedAt is null", familyId);
  }
}

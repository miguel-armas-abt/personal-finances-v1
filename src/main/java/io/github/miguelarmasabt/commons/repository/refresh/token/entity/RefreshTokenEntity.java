package io.github.miguelarmasabt.commons.repository.refresh.token.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "refresh_tokens")
public class RefreshTokenEntity {

  @BsonId
  private ObjectId id;

  private ObjectId userId;

  private String tokenHash;

  private String familyId;

  private Instant expiresAt;

  private Instant revokedAt;

  private ObjectId replacedByTokenId;

  private Instant createdAt;

  private Instant lastUsedAt;
}

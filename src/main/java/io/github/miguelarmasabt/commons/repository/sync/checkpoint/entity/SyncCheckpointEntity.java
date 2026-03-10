package io.github.miguelarmasabt.commons.repository.sync.checkpoint.entity;

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
@MongoEntity(collection = "sync_checkpoints")
public class SyncCheckpointEntity {

  @BsonId
  private ObjectId id;

  private String userCode;

  private Instant checkpointAt;

  private String scope;
}

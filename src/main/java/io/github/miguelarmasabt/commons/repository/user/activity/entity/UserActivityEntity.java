package io.github.miguelarmasabt.commons.repository.user.activity.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "user_activity")
public class UserActivityEntity {

  @BsonId
  private String userCode;

  private Instant lastSeenAt;
}

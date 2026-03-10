package io.github.miguelarmasabt.commons.repository.user.entity;

import io.github.miguelarmasabt.commons.repository.user.enums.AuthProvider;
import io.github.miguelarmasabt.commons.repository.user.enums.UserPlan;
import io.github.miguelarmasabt.commons.repository.user.enums.UserRole;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "users")
public class UserEntity {

  @BsonId
  private ObjectId id;

  private String email;

  private String fullName;

  private String pictureUrl;

  private String googleSub;

  public AuthProvider authProvider;

  public UserPlan plan = UserPlan.FREE;

  public Set<UserRole> roles = new HashSet<>();

  public boolean gmailConnected = false;

  public boolean enabled = true;

  public Instant createdAt;

  public Instant updatedAt;
}

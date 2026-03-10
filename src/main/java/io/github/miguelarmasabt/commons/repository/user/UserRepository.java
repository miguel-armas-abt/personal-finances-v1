package io.github.miguelarmasabt.commons.repository.user;

import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class UserRepository implements ReactivePanacheMongoRepositoryBase<UserEntity, ObjectId> {

  public Uni<Optional<UserEntity>> findByGoogleSub(String googleSub) {
    return find("googleSub", googleSub).firstResultOptional();
  }

  public Uni<Optional<UserEntity>> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  public Uni<Boolean> existsByEmail(String email) {
    return count("email", email)
        .map(count -> count > 0);
  }
}

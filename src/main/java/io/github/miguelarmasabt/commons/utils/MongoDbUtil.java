package io.github.miguelarmasabt.commons.utils;

import io.github.miguelarmasabt.personal.finances.expenses.crud.exceptions.InvalidObjectIdFormatException;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoDbUtil {

  public static Uni<ObjectId> validateAndGetObjectId(String id) {
    if (!ObjectId.isValid(id)) {
      return Uni.createFrom().failure(new InvalidObjectIdFormatException(id)
      );
    }
    return Uni.createFrom().item(new ObjectId(id));
  }

}

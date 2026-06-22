package io.github.miguelarmasabt.commons.repository.google;

import io.smallrye.mutiny.Uni;

public interface GoogleIdentityRepository {

  Uni<GoogleIdentity> verify(String idToken);

  record GoogleIdentity(
      String googleSub,
      String email,
      String fullName,
      String pictureUrl
  ) {
  }
}

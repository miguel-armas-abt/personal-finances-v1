package io.github.miguelarmasabt.personal.finances.auth.signin.service;

import io.github.miguelarmasabt.personal.finances.auth.signin.dto.GoogleIdentity;
import io.smallrye.mutiny.Uni;

public interface GoogleIdentityService {

  Uni<GoogleIdentity> verify(String idToken);
}

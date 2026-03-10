package io.github.miguelarmasabt.personal.finances.auth.signin.service;

import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.GoogleIdentity;
import io.smallrye.mutiny.Uni;

public interface GoogleUserProvisioningService {

  Uni<UserEntity> findOrProvisionGoogleUser(GoogleIdentity googleIdentity);
}

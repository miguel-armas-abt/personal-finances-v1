package io.github.miguelarmasabt.personal.finances.auth.session.service;

import io.github.miguelarmasabt.commons.repository.refresh.token.entity.RefreshTokenEntity;
import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;

public interface SessionService {

  Uni<AuthResponse> issueSession(UserEntity user);

  Uni<AuthResponse> rotateSession(UserEntity user, RefreshTokenEntity refreshToken);

  String hashRefreshToken(String refreshToken);
}

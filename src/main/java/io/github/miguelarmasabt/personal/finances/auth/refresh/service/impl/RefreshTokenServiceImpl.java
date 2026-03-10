package io.github.miguelarmasabt.personal.finances.auth.refresh.service.impl;

import io.github.miguelarmasabt.commons.repository.refresh.token.RefreshTokenRepository;
import io.github.miguelarmasabt.commons.repository.refresh.token.entity.RefreshTokenEntity;
import io.github.miguelarmasabt.commons.repository.user.UserRepository;
import io.github.miguelarmasabt.personal.finances.auth.refresh.dto.request.RefreshTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.refresh.exceptions.InvalidRefreshTokenException;
import io.github.miguelarmasabt.personal.finances.auth.refresh.service.RefreshTokenService;
import io.github.miguelarmasabt.personal.finances.auth.session.service.SessionService;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final SessionService sessionService;

  @Override
  public Uni<AuthResponse> refreshToken(RefreshTokenRequestDto request) {
    String tokenHash = sessionService.hashRefreshToken(request.getRefreshToken());

    return refreshTokenRepository.findByTokenHash(tokenHash)
        .onItem()
        .transformToUni(optionalToken -> optionalToken
            .map(this::refreshToken)
            .orElseGet(() -> Uni.createFrom().failure(new InvalidRefreshTokenException())));
  }

  private Uni<AuthResponse> refreshToken(RefreshTokenEntity refreshToken) {
    Instant now = Instant.now();

    if (Objects.nonNull(refreshToken.getRevokedAt())) {
      return refreshTokenRepository.revokeFamily(refreshToken.getFamilyId(), now)
          .chain(() -> Uni.createFrom().failure(new InvalidRefreshTokenException()));
    }

    return Optional.of(refreshToken)
        .filter(token -> Objects.nonNull(token.getExpiresAt()) && token.getExpiresAt().isAfter(now))
        .map(activeToken -> {
          activeToken.setLastUsedAt(now);
          return userRepository.findById(activeToken.getUserId())
              .onItem()
              .ifNull()
              .failWith(InvalidRefreshTokenException::new)
              .chain(user -> sessionService.rotateSession(user, activeToken));
        })
        .orElseGet(() -> Uni.createFrom().failure(new InvalidRefreshTokenException()));
  }
}

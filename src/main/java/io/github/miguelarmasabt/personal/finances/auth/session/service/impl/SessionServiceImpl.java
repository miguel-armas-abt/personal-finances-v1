package io.github.miguelarmasabt.personal.finances.auth.session.service.impl;

import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.repository.refresh.token.RefreshTokenRepository;
import io.github.miguelarmasabt.commons.repository.refresh.token.entity.RefreshTokenEntity;
import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.personal.finances.auth.session.mapper.JwtTokenMapper;
import io.github.miguelarmasabt.personal.finances.auth.session.service.SessionService;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.github.miguelarmasabt.personal.finances.auth.signin.exceptions.DisabledUserException;
import io.github.miguelarmasabt.personal.finances.auth.signin.mapper.SignInMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

  private static final int REFRESH_TOKEN_RANDOM_BYTES = 32;
  private static final String SHA_256 = "SHA-256";

  private final JwtTokenMapper jwtTokenMapper;
  private final SignInMapper signInMapper;
  private final RefreshTokenRepository refreshTokenRepository;
  private final ApplicationProperties properties;
  private final SecureRandom secureRandom = new SecureRandom();

  @ConfigProperty(name = "smallrye.jwt.new-token.lifespan")
  Long ttl;

  @Override
  public Uni<AuthResponse> issueSession(UserEntity user) {
    return Optional.of(user)
        .filter(UserEntity::isEnabled)
        .map(enabledUser -> issueSession(enabledUser, UUID.randomUUID().toString(), null))
        .orElseGet(() -> Uni.createFrom().failure(new DisabledUserException(user.getEmail())));
  }

  @Override
  public Uni<AuthResponse> rotateSession(UserEntity user, RefreshTokenEntity refreshToken) {
    return Optional.of(user)
        .filter(UserEntity::isEnabled)
        .map(enabledUser -> issueSession(enabledUser, refreshToken.getFamilyId(), refreshToken))
        .orElseGet(() -> Uni.createFrom().failure(new DisabledUserException(user.getEmail())));
  }

  @Override
  public String hashRefreshToken(String refreshToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance(SHA_256);
      byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  private Uni<AuthResponse> issueSession(UserEntity user, String familyId, RefreshTokenEntity previousRefreshToken) {
    String rawRefreshToken = generateRefreshToken();
    RefreshTokenEntity newRefreshToken = toRefreshTokenEntity(user, familyId, rawRefreshToken);

    return refreshTokenRepository.persist(newRefreshToken)
        .chain(persistedRefreshToken -> revokePreviousRefreshToken(previousRefreshToken, persistedRefreshToken)
            .replaceWith(buildAuthResponse(user, rawRefreshToken)));
  }

  private RefreshTokenEntity toRefreshTokenEntity(UserEntity user, String familyId, String rawRefreshToken) {
    Instant now = Instant.now();
    RefreshTokenEntity refreshToken = new RefreshTokenEntity();
    refreshToken.setUserId(user.getId());
    refreshToken.setTokenHash(hashRefreshToken(rawRefreshToken));
    refreshToken.setFamilyId(familyId);
    refreshToken.setExpiresAt(now.plus(properties.technical().auth().refreshToken().lifespanDays(), ChronoUnit.DAYS));
    refreshToken.setCreatedAt(now);
    refreshToken.setLastUsedAt(now);
    return refreshToken;
  }

  private Uni<RefreshTokenEntity> revokePreviousRefreshToken(RefreshTokenEntity previousRefreshToken,
                                                            RefreshTokenEntity newRefreshToken) {
    return Optional.ofNullable(previousRefreshToken)
        .map(token -> {
          token.setRevokedAt(Instant.now());
          token.setReplacedByTokenId(newRefreshToken.getId());
          return refreshTokenRepository.update(token);
        })
        .orElseGet(() -> Uni.createFrom().item(newRefreshToken));
  }

  private AuthResponse buildAuthResponse(UserEntity user, String refreshToken) {
    return new AuthResponse(
        jwtTokenMapper.generateAccessToken(user),
        refreshToken,
        ttl,
        signInMapper.toSession(user)
    );
  }

  private String generateRefreshToken() {
    byte[] randomBytes = new byte[REFRESH_TOKEN_RANDOM_BYTES];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(randomBytes);
  }
}

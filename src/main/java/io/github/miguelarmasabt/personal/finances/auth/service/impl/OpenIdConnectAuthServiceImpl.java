package io.github.miguelarmasabt.personal.finances.auth.service.impl;

import io.github.miguelarmasabt.commons.repository.google.GoogleIdentityRepository;
import io.github.miguelarmasabt.commons.repository.user.UserRepository;
import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.personal.finances.auth.dto.request.OpenIdAccessTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.dto.response.AuthResponse;
import io.github.miguelarmasabt.personal.finances.auth.mapper.AuthMapper;
import io.github.miguelarmasabt.personal.finances.auth.mapper.JwtTokenMapper;
import io.github.miguelarmasabt.personal.finances.auth.service.OpenIdConnectAuthService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class OpenIdConnectAuthServiceImpl implements OpenIdConnectAuthService {

  private final GoogleIdentityRepository googleIdentityRepository;
  private final UserRepository userRepository;
  private final JwtTokenMapper jwtTokenMapper;
  private final AuthMapper authMapper;

  @Override
  public Uni<AuthResponse> getAccessToken(OpenIdAccessTokenRequestDto request) {
    return googleIdentityRepository.verify(request.getIdToken())
        .onItem()
        .transformToUni(this::findOrCreateGoogleUser)
        .onItem()
        .transformToUni(this::validateEnabledUser)
        .onItem()
        .transform(this::buildAuthResponse);
  }

  private Uni<UserEntity> findOrCreateGoogleUser(GoogleIdentityRepository.GoogleIdentity googleIdentity) {
    return userRepository.findByGoogleSub(googleIdentity.googleSub())
        .onItem()
        .transformToUni(optionalUser -> optionalUser
            .map(user -> {
              authMapper.fillUserEntityWithGoogleProfile(user, googleIdentity);
              return userRepository.update(user);
            })
            .orElseGet(() -> createGoogleUserIfEmailIsAvailable(googleIdentity)));
  }

  private Uni<UserEntity> createGoogleUserIfEmailIsAvailable(GoogleIdentityRepository.GoogleIdentity googleIdentity) {
    return userRepository.findByEmail(googleIdentity.email())
        .onItem()
        .transformToUni(optionalUserByEmail -> {
          if (optionalUserByEmail.isPresent()) {
            return Uni.createFrom().failure(
                new WebApplicationException(
                    "Ya existe una cuenta con este correo. Inicia sesión con contraseña y vincula Google desde tu perfil.",
                    Response.Status.CONFLICT
                )
            );
          }

          UserEntity newUser = authMapper.toNewUserEntity(googleIdentity);
          return userRepository.persist(newUser);
        });
  }

  private Uni<UserEntity> validateEnabledUser(UserEntity user) {
    if (!user.isEnabled()) {
      return Uni.createFrom().failure(
          new ForbiddenException("Usuario deshabilitado")
      );
    }

    return Uni.createFrom().item(user);
  }

  private AuthResponse buildAuthResponse(UserEntity user) {
    String accessToken = jwtTokenMapper.generateAccessToken(user);

    return new AuthResponse(
        "Bearer",
        accessToken,
        1800,
        authMapper.toSession(user)
    );
  }

}

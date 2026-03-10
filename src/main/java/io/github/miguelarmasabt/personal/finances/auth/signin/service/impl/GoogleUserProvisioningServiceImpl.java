package io.github.miguelarmasabt.personal.finances.auth.signin.service.impl;

import io.github.miguelarmasabt.commons.repository.user.UserRepository;
import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.GoogleIdentity;
import io.github.miguelarmasabt.personal.finances.auth.signin.exceptions.AccountAlreadyExistsException;
import io.github.miguelarmasabt.personal.finances.auth.signin.mapper.SignInMapper;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleUserProvisioningService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class GoogleUserProvisioningServiceImpl implements GoogleUserProvisioningService {

  private final UserRepository userRepository;
  private final SignInMapper signInMapper;

  @Override
  public Uni<UserEntity> findOrProvisionGoogleUser(GoogleIdentity googleIdentity) {
    return userRepository.findByGoogleSub(googleIdentity.googleSub())
        .onItem()
        .transformToUni(optionalUser -> optionalUser
            .map(user -> syncGoogleProfile(user, googleIdentity))
            .orElseGet(() -> createGoogleUserIfEmailIsAvailable(googleIdentity)));
  }

  private Uni<UserEntity> syncGoogleProfile(UserEntity user, GoogleIdentity googleIdentity) {
    return signInMapper.syncGoogleProfile(user, googleIdentity)
        ? userRepository.update(user)
        : Uni.createFrom().item(user);
  }

  private Uni<UserEntity> createGoogleUserIfEmailIsAvailable(GoogleIdentity googleIdentity) {
    return userRepository.existsByEmail(googleIdentity.email())
        .onItem()
        .transformToUni(emailAlreadyExists -> Optional.of(emailAlreadyExists)
            .filter(Boolean::booleanValue)
            .<Uni<UserEntity>>map(ignored -> Uni.createFrom()
                .failure(new AccountAlreadyExistsException(googleIdentity.email())))
            .orElseGet(() -> userRepository.persist(signInMapper.toNewUserEntity(googleIdentity))));
  }
}

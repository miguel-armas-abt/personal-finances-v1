package io.github.miguelarmasabt.personal.finances.auth.signin.mapper;

import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.commons.repository.user.enums.AuthProvider;
import io.github.miguelarmasabt.commons.repository.user.enums.UserPlan;
import io.github.miguelarmasabt.commons.repository.user.enums.UserRole;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.GoogleIdentity;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mapper(config = MappingConfig.class)
public interface SignInMapper {

  default boolean syncGoogleProfile(UserEntity user, GoogleIdentity googleIdentity) {
    if (isSameGoogleProfile(user, googleIdentity)) {
      return false;
    }

    user.setEmail(googleIdentity.email());
    user.setFullName(googleIdentity.fullName());
    user.setPictureUrl(googleIdentity.pictureUrl());
    user.setUpdatedAt(Instant.now());
    return true;
  }

  private boolean isSameGoogleProfile(UserEntity user, GoogleIdentity googleIdentity) {
    return Objects.equals(user.getEmail(), googleIdentity.email())
        && Objects.equals(user.getFullName(), googleIdentity.fullName())
        && Objects.equals(user.getPictureUrl(), googleIdentity.pictureUrl());
  }

  @Mapping(target = "id", expression = "java(user.getId().toHexString())")
  @Mapping(target = "plan", expression = "java(user.getPlan().name())")
  AuthResponse.UserSession toSession(UserEntity user);

  default UserEntity toNewUserEntity(GoogleIdentity googleIdentity) {
    UserEntity newUser = new UserEntity();
    newUser.setEmail(googleIdentity.email());
    newUser.setFullName(googleIdentity.fullName());
    newUser.setPictureUrl(googleIdentity.pictureUrl());
    newUser.setGoogleSub(googleIdentity.googleSub());
    newUser.setAuthProvider(AuthProvider.GOOGLE);
    newUser.setPlan(UserPlan.FREE);
    newUser.setRoles(new HashSet<>(Set.of(UserRole.USER)));
    newUser.setGmailConnected(false);
    newUser.setEnabled(true);
    newUser.setCreatedAt(Instant.now());
    newUser.setUpdatedAt(Instant.now());
    return newUser;
  }
}

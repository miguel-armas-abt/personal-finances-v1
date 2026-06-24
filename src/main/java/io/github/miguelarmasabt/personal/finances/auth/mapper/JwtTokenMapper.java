package io.github.miguelarmasabt.personal.finances.auth.mapper;

import io.github.miguelarmasabt.commons.repository.user.entity.UserEntity;
import io.github.miguelarmasabt.commons.repository.user.enums.UserRole;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class JwtTokenMapper {

  public String generateAccessToken(UserEntity user) {
    Set<String> groups = user.getRoles()
        .stream()
        .map(UserRole::name)
        .collect(Collectors.toSet());

    return Jwt.subject(user.getId().toHexString())
        .upn(user.getEmail())
        .groups(groups)
        .claim("email", user.getEmail())
        .claim("plan", user.getPlan().name())
        .claim("auth_provider", user.getAuthProvider().name())
        .claim("gmail_connected", user.isGmailConnected())
        .sign();
  }
}

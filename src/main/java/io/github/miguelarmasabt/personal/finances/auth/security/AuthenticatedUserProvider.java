package io.github.miguelarmasabt.personal.finances.auth.security;

import io.github.miguelarmasabt.commons.exceptions.UserCodeRequiredException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

  private static final String EMAIL_CLAIM = "email";
  private static final String UPN_CLAIM = "upn";

  private final JsonWebToken jwt;

  public String userCode() {
    return Optional.ofNullable(getClaim(EMAIL_CLAIM))
        .filter(claim -> !claim.isBlank())
        .or(() -> Optional.ofNullable(getClaim(UPN_CLAIM))
            .filter(claim -> !claim.isBlank()))
        .orElseThrow(UserCodeRequiredException::new);
  }

  public String userId() {
    return jwt.getSubject();
  }

  private String getClaim(String claimName) {
    return jwt.getClaim(claimName);
  }
}

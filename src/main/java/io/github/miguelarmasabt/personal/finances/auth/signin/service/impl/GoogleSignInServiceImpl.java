package io.github.miguelarmasabt.personal.finances.auth.signin.service.impl;

import io.github.miguelarmasabt.personal.finances.auth.session.service.SessionService;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.request.SignInRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleIdentityService;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleSignInService;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleUserProvisioningService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class GoogleSignInServiceImpl implements GoogleSignInService {

  private final GoogleIdentityService googleIdentityService;
  private final GoogleUserProvisioningService googleUserProvisioningService;
  private final SessionService sessionService;

  @Override
  public Uni<AuthResponse> signIn(SignInRequestDto request) {
    return googleIdentityService.verify(request.getIdToken())
        .onItem()
        .transformToUni(googleUserProvisioningService::findOrProvisionGoogleUser)
        .onItem()
        .transformToUni(sessionService::issueSession);
  }
}

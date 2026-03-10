package io.github.miguelarmasabt.personal.finances.auth;

import io.github.miguelarmasabt.personal.finances.auth.refresh.service.RefreshTokenService;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleSignInService;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.AuthResource;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.RefreshTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.SignInRequestDto;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
@RequiredArgsConstructor
public class AuthRestService implements AuthResource {

  private final AuthRestMapper restMapper;

  private final GoogleSignInService googleSignInService;
  private final RefreshTokenService refreshTokenService;

  @Override
  public CompletionStage<Response> signInWithGoogle(String callerName,
                                                    String traceParent,
                                                    SignInRequestDto accessTokenRequest) {
    return Uni.createFrom().item(accessTokenRequest)
        .map(restMapper::toDto)
        .flatMap(request -> googleSignInService.signIn(request)
            .map(response -> Response.ok(response).build()))
        .subscribeAsCompletionStage();
  }

  @Override
  public CompletionStage<Response> refreshToken(String callerName,
                                                String traceParent,
                                                RefreshTokenRequestDto refreshTokenRequest) {
    return Uni.createFrom().item(refreshTokenRequest)
        .map(restMapper::toDto)
        .flatMap(request -> refreshTokenService.refreshToken(request)
            .map(response -> Response.ok(response).build()))
        .subscribeAsCompletionStage();
  }
}

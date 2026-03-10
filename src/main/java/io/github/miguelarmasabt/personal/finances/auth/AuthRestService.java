package io.github.miguelarmasabt.personal.finances.auth;

import io.github.miguelarmasabt.personal.finances.auth.dto.request.OpenIdAccessTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.service.OpenIdConnectAuthService;
import io.github.miguelarmasabt.personal.finances.expenses.rest.server.AuthResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
@RequiredArgsConstructor
public class AuthRestService implements AuthResource {

  private final OpenIdConnectAuthService openIdConnectAuthService;

  @Override
  public CompletionStage<Response> getAccessTokenBasedOnOpenIdConnect(String callerName,
                                                                      String traceParent,
                                                                      OpenIdAccessTokenRequestDto request) {
    return openIdConnectAuthService.getAccessToken(request)
        .map(response -> Response.ok(response).build())
        .subscribeAsCompletionStage();

  }
}

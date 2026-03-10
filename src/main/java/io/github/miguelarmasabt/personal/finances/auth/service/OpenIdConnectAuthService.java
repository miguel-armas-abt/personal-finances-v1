package io.github.miguelarmasabt.personal.finances.auth.service;

import io.github.miguelarmasabt.personal.finances.auth.dto.request.OpenIdAccessTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;

public interface OpenIdConnectAuthService {

  Uni<AuthResponse> getAccessToken(OpenIdAccessTokenRequestDto request);
}

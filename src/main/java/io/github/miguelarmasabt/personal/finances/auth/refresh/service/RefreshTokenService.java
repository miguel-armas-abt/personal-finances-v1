package io.github.miguelarmasabt.personal.finances.auth.refresh.service;

import io.github.miguelarmasabt.personal.finances.auth.refresh.dto.request.RefreshTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

public interface RefreshTokenService {

  Uni<AuthResponse> refreshToken(@Valid RefreshTokenRequestDto request);
}

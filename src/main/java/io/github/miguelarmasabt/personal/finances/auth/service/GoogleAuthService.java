package io.github.miguelarmasabt.personal.finances.auth.service;

import io.github.miguelarmasabt.personal.finances.auth.dto.request.GoogleLoginRequest;
import io.github.miguelarmasabt.personal.finances.auth.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;

public interface GoogleAuthService {

  Uni<AuthResponse> login(GoogleLoginRequest request);
}

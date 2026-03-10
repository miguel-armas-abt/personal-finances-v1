package io.github.miguelarmasabt.personal.finances.auth.signin.service;

import io.github.miguelarmasabt.personal.finances.auth.signin.dto.request.SignInRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

public interface GoogleSignInService {

  Uni<AuthResponse> signIn(@Valid SignInRequestDto request);
}

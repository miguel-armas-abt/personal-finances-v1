package io.github.miguelarmasabt.personal.finances.auth;

import io.github.miguelarmasabt.personal.finances.auth.dto.request.GoogleLoginRequest;
import io.github.miguelarmasabt.personal.finances.auth.dto.response.AuthResponse;
import io.github.miguelarmasabt.personal.finances.auth.service.GoogleAuthService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.RequiredArgsConstructor;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/auth")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthRestService {

  private final GoogleAuthService googleAuthService;

  @POST
  @Path("/google")
  public Uni<AuthResponse> loginWithGoogle(@Valid GoogleLoginRequest request) {
    return googleAuthService.login(request);
  }
}

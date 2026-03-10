package io.github.miguelarmasabt.commons.repository.sync.checkpoint.rest;

import io.github.miguelarmasabt.commons.repository.sync.checkpoint.SyncCheckpointRepository;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.personal.finances.auth.security.AuthenticatedUserProvider;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/user-activities")
@Authenticated
@RequiredArgsConstructor
public class SyncCheckpointRestService {

  private final SyncCheckpointRepository userActivityService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> findOrPersist() {
    return userActivityService.findOrPersist(authenticatedUserProvider.userCode(), SyncScope.EXPENSES)
        .map(result -> Response.ok()
            .entity(result)
            .build());
  }
}

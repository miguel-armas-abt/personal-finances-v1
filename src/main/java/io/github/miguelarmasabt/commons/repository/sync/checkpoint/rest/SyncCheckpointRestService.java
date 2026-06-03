package io.github.miguelarmasabt.commons.repository.sync.checkpoint.rest;

import io.github.miguelarmasabt.commons.dto.params.AppHeaders;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.SyncCheckpointRepository;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/user-activities")
@RequiredArgsConstructor
public class SyncCheckpointRestService {

  private final SyncCheckpointRepository userActivityService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> findOrPersist(@BeanParam @Valid AppHeaders headers) {
    return userActivityService.findOrPersist(headers.getUserCode(), SyncScope.EXPENSES)
        .map(result -> Response.ok()
            .entity(result)
            .build());
  }
}

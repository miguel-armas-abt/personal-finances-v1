package com.demo.service.finances.user.activity.rest;

import com.demo.service.commons.dto.params.AppHeaders;
import com.demo.service.finances.user.activity.service.UserActivityService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/poc/personal-finances/v1/user-activities")
@RequiredArgsConstructor
public class UserActivityRestService {

  private final UserActivityService userActivityService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> findOrPersist(@BeanParam @Valid AppHeaders headers) {
    return userActivityService.findOrPersist(headers.getUserCode())
        .map(result -> Response.ok()
            .entity(result)
            .build());
  }
}

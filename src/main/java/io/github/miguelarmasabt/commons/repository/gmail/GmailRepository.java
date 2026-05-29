package io.github.miguelarmasabt.commons.repository.gmail;

import io.github.miguelarmasabt.commons.repository.gmail.wrapper.response.MessageContentResponseWrapper;
import io.github.miguelarmasabt.commons.repository.gmail.wrapper.response.MessageResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/users/me/messages")
@RegisterRestClient(configKey = "gmail")
@Produces(MediaType.APPLICATION_JSON)
public interface GmailRepository {

  @GET
  Uni<MessageResponseWrapper> getMessages(
      @QueryParam("q") String q,
      @QueryParam("maxResults") long maxResults,
      @QueryParam("fields") String fields);

  @GET
  @Path("/{messageId}")
  Uni<MessageContentResponseWrapper> getMessageContent(
      @PathParam("messageId") String messageId,
      @QueryParam("format") String format);
}

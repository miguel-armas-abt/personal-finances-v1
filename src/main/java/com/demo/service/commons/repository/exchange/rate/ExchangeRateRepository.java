package com.demo.service.commons.repository.exchange.rate;

import com.demo.service.commons.repository.exchange.rate.wrapper.response.ExchangeRateResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rate")
@RegisterRestClient(configKey = "exchange-rate")
@Produces(MediaType.APPLICATION_JSON)
public interface ExchangeRateRepository {

  @GET
  @Path("/{base}/{quote}")
  Uni<ExchangeRateResponseWrapper> getExchangeRate(
      @PathParam("base") String base,
      @PathParam("quote") String quote);
}

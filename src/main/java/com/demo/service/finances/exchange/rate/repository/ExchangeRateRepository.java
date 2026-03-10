package com.demo.service.finances.exchange.rate.repository;

import com.demo.commons.interceptor.restclient.RestClientRequestInterceptor;
import com.demo.commons.interceptor.restclient.RestClientResponseInterceptor;
import com.demo.service.finances.exchange.rate.repository.wrapper.response.ExchangeRateResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rate")
@RegisterRestClient(configKey = "exchange-rate")
@RegisterClientHeaders(ExchangeRateHeaderFactory.class)
@RegisterProvider(RestClientRequestInterceptor.class)
@RegisterProvider(RestClientResponseInterceptor.class)
@Produces(MediaType.APPLICATION_JSON)
public interface ExchangeRateRepository {

  @GET
  @Path("/{base}/{quote}")
  Uni<ExchangeRateResponseWrapper> getExchangeRate(
      @PathParam("base") String base,
      @PathParam("quote") String quote);
}

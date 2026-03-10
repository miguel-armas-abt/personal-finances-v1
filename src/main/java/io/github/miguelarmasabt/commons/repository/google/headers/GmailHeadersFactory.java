package io.github.miguelarmasabt.commons.repository.google.headers;

import io.github.miguelarmasabt.constants.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
public class GmailHeadersFactory implements ClientHeadersFactory {

  private static final String GMAIL_ACCESS_TOKEN = "gmail-access-token";
  private static final String BEARER = "Bearer ";

  @Override
  public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
                                               MultivaluedMap<String, String> clientOutgoingHeaders) {

    MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

    String gmailAccessToken = incomingHeaders.getFirst(GMAIL_ACCESS_TOKEN);

    if (Strings.hasText(gmailAccessToken)) {
      headers.putSingle(HttpHeaders.AUTHORIZATION, BEARER.concat(gmailAccessToken));
    }

    return headers;
  }
}

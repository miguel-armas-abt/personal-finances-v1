package io.github.miguelarmasabt.personal.finances.auth.signin.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.error.exceptions.ProcessingFailedException;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.GoogleIdentity;
import io.github.miguelarmasabt.personal.finances.auth.signin.exceptions.GoogleEmailNotVerifiedException;
import io.github.miguelarmasabt.personal.finances.auth.signin.service.GoogleIdentityService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class GoogleIdentityServiceImpl implements GoogleIdentityService {

  private final ApplicationProperties properties;

  private GoogleIdTokenVerifier idTokenVerifier;

  @PostConstruct
  void init() {
    idTokenVerifier = new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance())
        .setAudience(List.of(properties.technical().google().openId().clientId()))
        .build();
  }

  @Override
  public Uni<GoogleIdentity> verify(String idToken) {
    return Uni.createFrom()
        .item(() -> verifyBlocking(idToken))
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
  }

  private GoogleIdentity verifyBlocking(String idToken) {
    try {
      GoogleIdToken token = idTokenVerifier.verify(idToken);

      GoogleIdToken.Payload payload = token.getPayload();

      if (!payload.getEmailVerified()) {
        throw new GoogleEmailNotVerifiedException(payload.getEmail());
      }

      return new GoogleIdentity(
          payload.getSubject(),
          payload.getEmail(),
          (String) payload.get("name"),
          (String) payload.get("picture")
      );

    } catch (GeneralSecurityException | IOException exception) {
      throw new ProcessingFailedException("5004", exception);
    }
  }
}

package io.github.miguelarmasabt.commons.repository.google.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.github.miguelarmasabt.commons.properties.ApplicationProperties;
import io.github.miguelarmasabt.commons.repository.google.GoogleIdentityRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
@RequiredArgsConstructor
public class GoogleIdentityRepositoryImpl implements GoogleIdentityRepository {

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

      if (Objects.isNull(token)) {
        throw new NotAuthorizedException("Invalid Google ID token");
      }

      GoogleIdToken.Payload payload = token.getPayload();

      if (!payload.getEmailVerified()) {
        throw new NotAuthorizedException("El correo de Google no está verificado");
      }

      return new GoogleIdentity(
          payload.getSubject(),
          payload.getEmail(),
          (String) payload.get("name"),
          (String) payload.get("picture")
      );

    } catch (GeneralSecurityException | IOException e) {
      throw new NotAuthorizedException("No se pudo validar el token de Google", e);
    }
  }
}

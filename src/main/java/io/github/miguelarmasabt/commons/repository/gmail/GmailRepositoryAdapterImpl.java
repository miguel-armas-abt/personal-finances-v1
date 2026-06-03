package io.github.miguelarmasabt.commons.repository.gmail;

import io.github.miguelarmasabt.commons.properties.GmailRestClientProperties;
import io.github.miguelarmasabt.repository.gmail.api.GmailRepository;
import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.github.miguelarmasabt.repository.gmail.model.MessageResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@RequiredArgsConstructor
public class GmailRepositoryAdapterImpl implements GmailRepositoryAdapter {

  private final GmailRestClientProperties properties;

  @RestClient
  GmailRepository gmailRepository;

  @Override
  public Uni<MessageResponseWrapper> getMessages(String query) {
    Long pageSize = properties.messages().pageSize();
    String fields = properties.messages().fields();
    return gmailRepository.getMessages(query, pageSize, fields);
  }

  @Override
  public Uni<MessageContentResponseWrapper> getMessageContent(String messageId) {
    String format = properties.messageContent().format();
    return gmailRepository.getMessageContent(messageId, format);
  }
}

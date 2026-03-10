package io.github.miguelarmasabt.commons.repository.google;

import io.github.miguelarmasabt.commons.repository.google.model.MessageContentResponseWrapper;
import io.github.miguelarmasabt.commons.repository.google.model.MessageResponseWrapper;
import io.smallrye.mutiny.Uni;

public interface GmailRepositoryAdapter {

  Uni<MessageResponseWrapper> getMessages(String query);

  Uni<MessageContentResponseWrapper> getMessageContent(String messageId);
}

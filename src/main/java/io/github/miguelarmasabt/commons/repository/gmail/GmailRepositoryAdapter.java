package io.github.miguelarmasabt.commons.repository.gmail;

import io.github.miguelarmasabt.repository.gmail.model.MessageContentResponseWrapper;
import io.github.miguelarmasabt.repository.gmail.model.MessageResponseWrapper;
import io.smallrye.mutiny.Uni;

public interface GmailRepositoryAdapter {

  Uni<MessageResponseWrapper> getMessages(String query);

  Uni<MessageContentResponseWrapper> getMessageContent(String messageId);
}

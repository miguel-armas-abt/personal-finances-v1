package io.github.miguelarmasabt.commons.properties;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@StaticInitSafe
@ConfigMapping(prefix = "quarkus.rest-client.gmail.params")
public interface GmailRestClientProperties {

  Messages messages();

  MessageContent messageContent();

  interface Messages {
    @WithDefault("10")
    Long pageSize();

    @WithDefault("messages/id,nextPageToken")
    String fields();
  }

  interface MessageContent {
    @WithDefault("full")
    String format();
  }
}

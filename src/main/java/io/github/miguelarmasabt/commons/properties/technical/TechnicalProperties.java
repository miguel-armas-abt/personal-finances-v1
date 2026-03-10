package io.github.miguelarmasabt.commons.properties.technical;

import io.smallrye.config.WithDefault;

public interface TechnicalProperties {

  Auth auth();

  Csv csv();

  Google google();

  interface Auth {
    RefreshToken refreshToken();

    interface RefreshToken {
      @WithDefault("30")
      Long lifespanDays();
    }
  }

  interface Csv {

    Export exports();

    Import imports();

    interface Import {
      @WithDefault("500")
      Integer batchSize();
    }

    interface Export {
      @WithDefault("250")
      Integer rowsPerChunk();
    }
  }

  interface Google {
    OpenId openId();

    interface OpenId {
      String clientId();
    }
  }
}

package io.github.miguelarmasabt.commons.properties.technical;

import io.smallrye.config.WithDefault;

public interface TechnicalProperties {

  CsvProperties csv();

  interface CsvProperties {

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
}

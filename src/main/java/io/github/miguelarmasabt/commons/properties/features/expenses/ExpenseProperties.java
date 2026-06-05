package io.github.miguelarmasabt.commons.properties.features.expenses;

import io.smallrye.config.WithDefault;

public interface ExpenseProperties {

  CsvProperties csv();

  SearchCriteria searchCriteria();

  Sync sync();

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

  interface SearchCriteria {
    @WithDefault("20")
    Integer pageSize();
  }

  interface Sync {
    @WithDefault("30")
    Long initialCheckpointLookbackDays();
  }
}

package io.github.miguelarmasabt.commons.properties.features.expenses;

import io.smallrye.config.WithDefault;

import java.util.Map;

public interface ExpenseProperties {

  CsvProperties csv();

  SearchCriteria searchCriteria();

  Refresh refresh();

  Map<String, BankReceipt> bankReceipts();

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

  interface BankReceipt {
    String from();

    String subject();
  }

  interface Refresh {
    @WithDefault("30")
    Long initialCheckpointLookbackDays();
  }
}

package io.github.miguelarmasabt.commons.properties.business.expenses;

import io.smallrye.config.WithDefault;

public interface ExpenseProperties {

  SearchCriteria searchCriteria();

  Sync sync();

  interface SearchCriteria {
    @WithDefault("20")
    Integer pageSize();
  }

  interface Sync {
    @WithDefault("30")
    Long initialCheckpointLookbackDays();
  }
}

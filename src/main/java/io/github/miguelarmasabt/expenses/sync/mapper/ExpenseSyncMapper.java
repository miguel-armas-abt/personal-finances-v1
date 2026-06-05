package io.github.miguelarmasabt.expenses.sync.mapper;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.expenses.rest.server.beans.ExpenseRefreshResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface ExpenseSyncMapper {

  default ExpenseRefreshResponseDto toResponse(List<String> gmailMessageIds) {
    ExpenseRefreshResponseDto response = new ExpenseRefreshResponseDto();
    response.setGmailMessageIds(gmailMessageIds);
    return response;
  }
}

package io.github.miguelarmasabt.personal.finances.expenses.crud.mapper;

import io.github.miguelarmasabt.commons.utils.DateUtil;
import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import io.github.miguelarmasabt.personal.finances.expenses.crud.repository.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, imports = {
    DateUtil.class
})
public interface ExpenseUpdateMapper {

  @Mapping(target = "date", expression = "java(DateUtil.toInstantAtTime(updateRequest.getDate()))")
  ExpenseEntity toUpdateEntity(String userCode, ExpenseUpdateRequestDto updateRequest);

}

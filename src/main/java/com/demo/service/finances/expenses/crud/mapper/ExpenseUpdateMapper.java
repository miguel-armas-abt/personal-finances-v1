package com.demo.service.finances.expenses.crud.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.commons.utils.DateUtil;
import com.demo.service.finances.expenses.crud.dto.request.ExpenseUpdateRequestDto;
import com.demo.service.finances.expenses.crud.repository.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, imports = {
    DateUtil.class
})
public interface ExpenseUpdateMapper {

  @Mapping(target = "date", expression = "java(DateUtil.toInstantAtTime(updateRequest.getDate()))")
  ExpenseEntity toUpdateEntity(String userCode, ExpenseUpdateRequestDto updateRequest);

}

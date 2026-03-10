package com.demo.service.finances.user.activity.mapper;

import com.demo.commons.config.mapper.MappingConfig;
import com.demo.service.finances.user.activity.dto.response.UserActivityResponseDto;
import com.demo.service.finances.user.activity.repository.entity.UserActivityEntity;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(config = MappingConfig.class, imports = {
    Instant.class
})
public interface UserActivityMapper {

  UserActivityResponseDto toDto(UserActivityEntity userActivity);

  UserActivityEntity toEntity(String userCode, Instant lastSeenAt);
}

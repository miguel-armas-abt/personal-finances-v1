package com.demo.service.commons.repository.user.activity.mapper;

import com.demo.commons.config.MappingConfig;
import com.demo.service.commons.repository.user.activity.entity.UserActivityEntity;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(config = MappingConfig.class, imports = {
    Instant.class
})
public interface UserActivityMapper {

  UserActivityEntity toEntity(String userCode, Instant lastSeenAt);
}

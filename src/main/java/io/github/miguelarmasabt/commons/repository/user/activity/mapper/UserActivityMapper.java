package io.github.miguelarmasabt.commons.repository.user.activity.mapper;

import io.github.miguelarmasabt.commons.repository.user.activity.entity.UserActivityEntity;
import io.github.miguelarmasabt.config.MappingConfig;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(config = MappingConfig.class, imports = {
    Instant.class
})
public interface UserActivityMapper {

  UserActivityEntity toEntity(String userCode, Instant lastSeenAt);
}

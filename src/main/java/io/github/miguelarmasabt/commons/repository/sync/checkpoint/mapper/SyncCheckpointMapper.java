package io.github.miguelarmasabt.commons.repository.sync.checkpoint.mapper;

import io.github.miguelarmasabt.commons.repository.sync.checkpoint.entity.SyncCheckpointEntity;
import io.github.miguelarmasabt.commons.repository.sync.checkpoint.enums.SyncScope;
import io.github.miguelarmasabt.config.MappingConfig;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(config = MappingConfig.class, imports = {
    Instant.class
})
public interface SyncCheckpointMapper {

  SyncCheckpointEntity toEntity(String userCode, SyncScope scope, Instant checkpointAt);
}

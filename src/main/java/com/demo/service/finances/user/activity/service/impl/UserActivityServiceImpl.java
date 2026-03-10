package com.demo.service.finances.user.activity.service.impl;

import com.demo.service.finances.user.activity.dto.response.UserActivityResponseDto;
import com.demo.service.finances.user.activity.mapper.UserActivityMapper;
import com.demo.service.finances.user.activity.repository.UserActivityRepository;
import com.demo.service.finances.user.activity.repository.entity.UserActivityEntity;
import com.demo.service.finances.user.activity.service.UserActivityService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

  private final UserActivityRepository repository;
  private final UserActivityMapper mapper;

  @Override
  public Uni<UserActivityResponseDto> findOrPersist(String userCode) {
    return repository.findOrPersist(userCode)
        .map(mapper::toDto);
  }

  @Override
  public Uni<Void> update(String userCode, Instant instant) {
    UserActivityEntity userActivity = mapper.toEntity(userCode, instant);
    return repository.updateByUserCode(userActivity);
  }
}

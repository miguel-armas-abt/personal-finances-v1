package com.demo.service.finances.user.activity.service;

import com.demo.service.finances.user.activity.dto.response.UserActivityResponseDto;
import io.smallrye.mutiny.Uni;

import java.time.Instant;

public interface UserActivityService {

  Uni<UserActivityResponseDto> findOrPersist(String userCode);

  Uni<Void> update(String userCode, Instant instant);
}

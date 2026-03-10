package io.github.miguelarmasabt.personal.finances.auth;

import io.github.miguelarmasabt.config.MappingConfig;
import io.github.miguelarmasabt.personal.finances.auth.refresh.dto.request.RefreshTokenRequestDto;
import io.github.miguelarmasabt.personal.finances.auth.signin.dto.request.SignInRequestDto;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface AuthRestMapper {

  SignInRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.SignInRequestDto accessTokenRequest);

  RefreshTokenRequestDto toDto(io.github.miguelarmasabt.personal.finances.expenses.rest.server.beans.RefreshTokenRequestDto refreshTokenRequest);
}

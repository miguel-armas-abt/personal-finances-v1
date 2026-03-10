package io.github.miguelarmasabt.personal.finances.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdAccessTokenRequestDto implements Serializable {

  @NotBlank
  String idToken;

}

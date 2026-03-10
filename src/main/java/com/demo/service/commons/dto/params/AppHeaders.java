package com.demo.service.commons.dto.params;

import com.demo.commons.restserver.DefaultHeaders;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppHeaders extends DefaultHeaders {

  public static final String USER_CODE = "userCode";

  @HeaderParam("Authorization")
  @NotBlank
  String authorization;

  @HeaderParam(USER_CODE)
  @Email
  @NotBlank
  String userCode;
}

package io.github.miguelarmasabt.personal.finances.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse implements Serializable {

  private String tokenType;
  private String accessToken;
  private long expiresIn;
  private UserSession user;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserSession {
    private String id;
    private String email;
    private String fullName;
    private String pictureUrl;
    private String plan;
    private boolean gmailConnected;
  }
}

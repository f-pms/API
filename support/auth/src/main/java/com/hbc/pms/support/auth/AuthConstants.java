package com.hbc.pms.support.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthConstants {
  public static final String USER_ID = "userId";
  public static final String USER_ROLE = "role";
  public static final String LOGIN_PATH = "/auth/login";
  public static final String WEBSOCKET_PATH = "/websocket/**";
  public static final String AUTHORIZATION_HEADER = "Authorization";
}

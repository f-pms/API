package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.api.support.error.ForbiddenException;
import com.hbc.pms.core.model.User;
import com.hbc.pms.core.model.enums.Role;
import com.hbc.pms.support.auth.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserValidationService {
  private final AuthenticationFacade authenticationFacade;

  public void authorizeUpdate(User user) {
    if (authenticationFacade.hasRole(Role.ADMIN.name())) {
      // Admin can update any user
      return;
    }
    if (!authenticationFacade.getUserId().equals(user.getId().toString())) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, "You can only update your own user");
    }
  }

  public void authorizeDelete(User user) {
    if (authenticationFacade.getUserId().equals(user.getId().toString())) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, "You can't delete your own user");
    }
  }

  public void authorizeGet(Long userId) {
    if (authenticationFacade.hasRole(Role.SUPERVISOR.name())
        && !authenticationFacade.getUserId().equals(userId.toString())) {
      throw new ForbiddenException();
    }
  }
}

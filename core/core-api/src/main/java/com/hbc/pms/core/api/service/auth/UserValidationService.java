package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.api.controller.v1.request.auth.UpdateUserCommand;
import com.hbc.pms.core.model.User;
import com.hbc.pms.core.model.enums.Role;
import com.hbc.pms.support.auth.AuthenticationFacade;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import com.hbc.pms.support.web.error.ForbiddenException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserValidationService {
  private final AuthenticationFacade authenticationFacade;
  private final PasswordEncoder passwordEncoder;
  private final UserPersistenceService userPersistenceService;

  public void validateUpdateCommand(User toUpdate, UpdateUserCommand updateUserCommand) {
    validateUpdatePassword(toUpdate, updateUserCommand);
    validateEmail(toUpdate, updateUserCommand);
  }

  private boolean verifyPasswordExistence(UpdateUserCommand updateUserCommand) {
    boolean passwordPresent = StringUtils.isNoneEmpty(updateUserCommand.getPassword());
    boolean oldPasswordPresent = StringUtils.isNoneEmpty(updateUserCommand.getOldPassword());
    return passwordPresent == oldPasswordPresent;
  }

  private boolean verifyOldPasswordMatch(User toUpdate, UpdateUserCommand updateUserCommand) {
    return passwordEncoder.matches(updateUserCommand.getOldPassword(), toUpdate.getPassword());
  }

  public void authorizeUpdate(User state) {
    if (authenticationFacade.hasRole(Role.ADMIN.name())) {
      // Admin can update any user
      return;
    }
    if (!authenticationFacade.getUserId().equals(state.getId().toString())) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, "You can only update your own user");
    }
  }

  public void validateEmail(User toUpdate, UpdateUserCommand updateUserCommand) {
    if (StringUtils.isNoneEmpty(updateUserCommand.getEmail())
        && isNotSelfEmail(toUpdate, updateUserCommand)) {
      if (userPersistenceService.findByEmail(updateUserCommand.getEmail()).isPresent()) {
        throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, "Email already exists");
      }
    }
  }

  private boolean isNotSelfEmail(User toUpdate, UpdateUserCommand updateUserCommand) {
    return !toUpdate.getEmail().equals(updateUserCommand.getEmail());
  }

  public void validateUpdatePassword(User toUpdate, UpdateUserCommand updateUserCommand) {
    if (authenticationFacade.hasRole(Role.ADMIN.name())) {
      // Admin can bypass password check
      return;
    }
    if (!verifyPasswordExistence(updateUserCommand)) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, "Both password and old password must be present or absent");
    }
    if (StringUtils.isNotEmpty(updateUserCommand.getOldPassword())
        && !verifyOldPasswordMatch(toUpdate, updateUserCommand)) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, "The current password is not correct");
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

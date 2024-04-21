package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.api.constant.ErrorMessageConstant;
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

  public void validateCreate(User userToCreate) {
    verifyMailExist(userToCreate.getEmail());
    verifyUsernameExist(userToCreate.getUsername());
  }

  private void verifyMailExist(String email) {
    if (userPersistenceService.findByEmail(email).isPresent()) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.EXISTED_EMAIL);
    }
  }

  private void verifyUsernameExist(String username) {
    if (userPersistenceService.findByUsernameOptional(username).isPresent()) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.EXISTED_USERNAME);
    }
  }

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
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.ONLY_UPDATE_YOUR_OWN_USER);
    }
  }

  public void validateEmail(User toUpdate, UpdateUserCommand updateUserCommand) {
    if (StringUtils.isNoneEmpty(updateUserCommand.getEmail())
        && isNotSelfEmail(toUpdate, updateUserCommand)) {
      verifyMailExist(updateUserCommand.getEmail());
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
          ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.BOTH_NEW_AND_OLD_PASS_MUST_BE_PRESENT_OR_ABSENT);
    }
    if (StringUtils.isNotEmpty(updateUserCommand.getOldPassword())
        && !verifyOldPasswordMatch(toUpdate, updateUserCommand)) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.CURRENT_PASS_IS_NOT_CORRECT);
    }
  }

  public void authorizeDelete(User user) {
    if (authenticationFacade.getUserId().equals(user.getId().toString())) {
      throw new CoreApiException(ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.YOU_CAN_NOT_DELETE_YOUR_OWN);
    }
  }

  public void authorizeGet(Long userId) {
    if (authenticationFacade.hasRole(Role.SUPERVISOR.name())
        && !authenticationFacade.getUserId().equals(userId.toString())) {
      throw new ForbiddenException();
    }
  }
}

package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.constant.ErrorMessageConstant
import com.hbc.pms.core.api.controller.v1.request.auth.UpdateUserCommand
import com.hbc.pms.core.api.service.auth.UserPersistenceService
import com.hbc.pms.core.api.service.auth.UserValidationService
import com.hbc.pms.core.model.User
import com.hbc.pms.core.model.enums.Role
import com.hbc.pms.support.auth.AuthenticationFacade
import com.hbc.pms.support.web.error.CoreApiException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserValidationServiceSpec extends Specification {
  AuthenticationFacade authenticationFacade = Mock()
  PasswordEncoder passwordEncoder = Mock()
  UserPersistenceService userPersistenceService = Mock()
  UserValidationService userValidationService
  User supervisorUser = TestDataFixture.createRandomUser(Role.SUPERVISOR)
  String newEmail = "newEmail@gmail.com"
  String existingEmail = "existing@gmail.com"
  String oldPassword = "oldPassword"

  def setup() {
    userValidationService = new UserValidationService(authenticationFacade,
            passwordEncoder,
            userPersistenceService)
    passwordEncoder.matches(oldPassword, _ as String) >> true
  }


  def "Admin can bypass password check - OK"() {
    given: "A user and an update command"
    UpdateUserCommand command = UpdateUserCommand.builder()
            .email(newEmail)
            .fullName("Updated Full name")
            .password("updated")
            .build()

    and: "User with new email does not exist"
    userPersistenceService.findByEmail(newEmail) >> Optional.empty()

    and: "Admin is logged in"
    authenticationFacade.hasRole(Role.ADMIN.name()) >> true

    when: "Validate update command is called"
    userValidationService.validateUpdateCommand(supervisorUser, command)

    then: "No exception is thrown"
    noExceptionThrown()
  }

  def "#user Fails on mail check - fail"() {
    given: "A user and an update command"
    UpdateUserCommand command = UpdateUserCommand.builder()
            .email(existingEmail)
            .build()

    and: "User with new email does exist"
    userPersistenceService.findByEmail(existingEmail) >> Optional.of(supervisorUser)

    and: "Admin is logged in"
    authenticationFacade.hasRole(user) >> true

    when: "Validate update command is called"
    userValidationService.validateUpdateCommand(supervisorUser, command)

    then: "Exception is thrown"
    def exception = thrown(CoreApiException)
    exception.data == ErrorMessageConstant.EXISTED_EMAIL

    where:
    user << [Role.ADMIN.name(), Role.SUPERVISOR.name()]
  }

  def "Valid password and old password update - should pass"() {
    given: "A user and an update command with valid passwords"
    UpdateUserCommand command = UpdateUserCommand.builder()
            .password("newPassword")
            .oldPassword(oldPassword)
            .build()
    authenticationFacade.hasRole(Role.SUPERVISOR.name()) >> true

    when: "Validate update command is called"
    userValidationService.validateUpdateCommand(supervisorUser, command)

    then: "No exception is thrown"
    noExceptionThrown()
  }

  def "Valid password and incorrect old password update - should fail"() {
    given: "A user and an update command with valid passwords"
    UpdateUserCommand command = UpdateUserCommand.builder()
            .password("newPassword")
            .oldPassword("Incorrect_password")
            .build()
    authenticationFacade.hasRole(Role.SUPERVISOR.name()) >> true

    when: "Validate update command is called"
    userValidationService.validateUpdateCommand(supervisorUser, command)

    then:
    def exception = thrown(CoreApiException)
    exception.data == ErrorMessageConstant.CURRENT_PASS_IS_NOT_CORRECT
  }


}

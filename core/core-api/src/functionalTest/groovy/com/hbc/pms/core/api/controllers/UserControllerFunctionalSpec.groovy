package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.controller.v1.request.auth.CreateUserCommand
import com.hbc.pms.core.api.controller.v1.request.auth.UpdateUserCommand
import com.hbc.pms.core.model.User
import com.hbc.pms.core.model.enums.Role
import com.hbc.pms.integration.db.repository.UserRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

class UserControllerFunctionalSpec extends FunctionalTestSpec {
  protected static String USER_PATH = "/users"
  @Autowired
  RestClient restClient
  @Autowired
  UserRepository userRepository

  @Autowired
  PasswordEncoder passwordEncoder
  def validCreateCommandBuilder = CreateUserCommand.builder()
          .email("test@gmail.com")
          .fullName("Full name")
          .password("123")
          .username("validUser")
          .role(Role.SUPERVISOR)

  def "#user create SUPERVISOR - #expectedStatusCode "() {
    given:
    def validCommand = validCreateCommandBuilder.build()
    UserDetails userDetails = dataFixture."${user}"
    when: "Post create as an admin with the valid command"
    def response = restClient.post(USER_PATH, validCommand, userDetails, User)

    then:
    response.statusCode.value() == expectedStatusCode.value()
    if (expectedStatusCode == HttpStatus.OK) {
      def createdUser = userRepository.findById((Long) response.body.data["id"])
      assert createdUser.isPresent()
      assert createdUser.get().email == validCommand.email
      assert passwordEncoder.matches(validCommand.password, createdUser.get().password)
    }
    where:
    user              | expectedStatusCode
    "ADMIN_USER"      | HttpStatus.OK
    "SUPERVISOR_USER" | HttpStatus.FORBIDDEN
  }

  def "#user create SUPERVISOR with invalid command- #expectedStatusCode "() {
    given:
    UserDetails userDetails = dataFixture."${user}"
    def invalidCommand = validCreateCommandBuilder.build()
    invalidCommand.setEmail("invalid email")
    invalidCommand.setPassword("")
    invalidCommand.setUsername("s")

    when: "Post create as an admin with the valid command"
    def response = restClient.post(USER_PATH, invalidCommand, userDetails, Object)

    then:
    response.statusCode.value() == expectedStatusCode.value()
    Map<String, String> errorCodes = response.body.error.data as Map<String, String>
    assert errorCodes.size() == 3
    assert errorCodes.containsKey("email")
    assert errorCodes.containsKey("password")
    assert errorCodes.containsKey("username")
    where:
    user         | expectedStatusCode
    "ADMIN_USER" | HttpStatus.BAD_REQUEST
  }

  def "#user self update user info - #expectedStatusCode"() {
    given:
    User userDetails = dataFixture."${user}"
    def userId = userDetails.id
    def updateCommand = UpdateUserCommand.builder()
            .email("updated@gmail.com")
            .fullName("Updated Full name")
            .password("updated")
            .build()

    when: "Put update with the update command"
    def response = restClient.put("$USER_PATH/$userId", updateCommand, userDetails, User)

    then:
    response.statusCode.value() == expectedStatusCode.value()
    if (response.statusCode.value() == HttpStatus.OK.value()) {
      def updatedUser = userRepository.findById(userId)
      assert updatedUser.isPresent()
      assert updatedUser.get().email == updateCommand.email
      assert passwordEncoder.matches(updateCommand.password, updatedUser.get().password)
      assert updatedUser.get().fullName == updateCommand.fullName
    }
    where:
    user              | expectedStatusCode
    "ADMIN_USER"      | HttpStatus.OK
    "SUPERVISOR_USER" | HttpStatus.BAD_REQUEST
  }

}

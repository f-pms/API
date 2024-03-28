package com.hbc.pms.core.api.controller.v1;

import static com.hbc.pms.core.api.config.auth.AuthorizationExpressions.HAS_ROLE_ADMIN;

import com.hbc.pms.core.api.controller.v1.request.auth.CreateUserCommand;
import com.hbc.pms.core.api.controller.v1.request.auth.QueryUserCommand;
import com.hbc.pms.core.api.controller.v1.request.auth.UpdateUserCommand;
import com.hbc.pms.core.api.service.auth.UserService;
import com.hbc.pms.core.model.User;
import com.hbc.pms.support.web.pagination.QueryResult;
import com.hbc.pms.support.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final ModelMapper modelMapper;

  @GetMapping
  public ApiResponse<QueryResult<User>> query(QueryUserCommand queryUserCommand) {
    return ApiResponse.success(userService.query(queryUserCommand));
  }

  @GetMapping("/{userId}")
  public ApiResponse<User> get(@PathVariable Long userId) {
    return ApiResponse.success(userService.findById(userId));
  }

  @PostMapping
  @PreAuthorize(HAS_ROLE_ADMIN)
  public ApiResponse<User> create(@RequestBody @Valid CreateUserCommand createUserCommand) {
    return ApiResponse.success(userService.create(modelMapper.map(createUserCommand, User.class)));
  }

  @PutMapping("/{userId}")
  public ApiResponse<User> update(
      @PathVariable Long userId, @RequestBody UpdateUserCommand updateUserCommand) {
    User updatedUser = userService.update(userId, modelMapper.map(updateUserCommand, User.class));
    return ApiResponse.success(updatedUser);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize(HAS_ROLE_ADMIN)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long userId) {
    userService.delete(userId);
  }
}

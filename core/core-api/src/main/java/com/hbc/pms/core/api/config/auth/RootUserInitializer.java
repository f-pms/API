package com.hbc.pms.core.api.config.auth;

import com.hbc.pms.core.api.service.auth.UserPersistenceService;
import com.hbc.pms.core.model.User;
import com.hbc.pms.core.model.enums.Role;
import com.hbc.pms.integration.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RootUserInitializer implements ApplicationRunner {
  private final UserRepository userRepository;
  private final UserPersistenceService userPersistenceService;
  private final RootUserProperties rootUserProperties;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.count() == 0) {
      User userToCreate =
          User.builder()
              .email("admin@gmail.com")
              .role(Role.ADMIN)
              .fullName("Administrator")
              .username(rootUserProperties.getUsername())
              .password(rootUserProperties.getPassword())
              .build();
      userPersistenceService.create(userToCreate);
    }
  }
}

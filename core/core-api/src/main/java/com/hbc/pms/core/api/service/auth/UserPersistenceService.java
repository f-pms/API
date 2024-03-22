package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.model.User;
import com.hbc.pms.integration.db.entity.UserEntity;
import com.hbc.pms.integration.db.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserPersistenceService {
  private final UserRepository userRepository;
  private final ModelMapper mapper;

  public User findByUsername(String username) {
    UserEntity entity =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Can't found user with username:" + username));
    return mapper.map(entity, User.class);
  }
}

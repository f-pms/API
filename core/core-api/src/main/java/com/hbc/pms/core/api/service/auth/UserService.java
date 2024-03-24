package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.model.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
  private final UserPersistenceService userPersistenceService;
  private final UserValidationService userValidationService;
  private final ModelMapper modelMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userPersistenceService.findByUsername(username);
  }

  public User findById(Long userId) {
    userValidationService.authorizeGet(userId);
    return userPersistenceService.findById(userId);
  }

  public User create(User userToCreate) {
    return userPersistenceService.create(userToCreate);
  }

  public User update(Long userId, User state) {
    state.setId(userId);
    userValidationService.authorizeUpdate(state);
    User toUpdate = userPersistenceService.findById(userId);
    modelMapper.map(state, toUpdate);
    return userPersistenceService.update(toUpdate);
  }

  public void delete(Long userId) {
    User userToDelete = userPersistenceService.findById(userId);
    userValidationService.authorizeDelete(userToDelete);
    userPersistenceService.delete(userToDelete.getId());
  }
}

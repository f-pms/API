package com.hbc.pms.core.api.service.auth;

import static com.hbc.pms.core.api.util.StringUtil.isStringEncoded;

import com.hbc.pms.core.api.service.AbstractPersistenceService;
import com.hbc.pms.core.model.User;
import com.hbc.pms.integration.db.entity.UserEntity;
import com.hbc.pms.integration.db.repository.UserRepository;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPersistenceService extends AbstractPersistenceService<UserEntity> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public Page<User> query(Pageable pageable) {
    var page = userRepository.findAll(pageable);
    return page.map(entity -> mapToModel(entity, User.class));
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email).map(entity -> mapToModel(entity, User.class));
  }

  public User findById(Long id) {
    UserEntity entity =
        userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new CoreApiException(
                        ErrorType.NOT_FOUND_ERROR, "Can't found user with id:" + id));
    return mapper.map(entity, User.class);
  }

  public User findByUsername(String username) {
    UserEntity entity =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Can't found user with username:" + username));
    return mapper.map(entity, User.class);
  }

  public Optional<User> findByUsernameOptional(String username) {
    return userRepository.findByUsername(username).map(entity -> mapToModel(entity, User.class));
  }

  public User create(User userToCreate) {
    encodePassword(userToCreate);
    UserEntity entity = mapper.map(userToCreate, UserEntity.class);
    return mapper.map(userRepository.save(entity), User.class);
  }

  public User update(User state) {
    encodePassword(state);
    UserEntity entity = mapper.map(state, UserEntity.class);
    return mapper.map(userRepository.save(entity), User.class);
  }

  public void delete(Long id) {
    userRepository.deleteById(id);
  }

  private void encodePassword(User user) {
    if (StringUtils.isNotEmpty(user.getPassword()) && !isStringEncoded(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
  }
}

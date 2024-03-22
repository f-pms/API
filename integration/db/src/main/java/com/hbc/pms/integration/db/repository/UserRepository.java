package com.hbc.pms.integration.db.repository;

import com.hbc.pms.integration.db.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
}

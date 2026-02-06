package com.topcit.aims.aims.domain.repository;

import com.topcit.aims.aims.domain.model.User;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Integer> {
    Optional<UserJpaEntity> findByUsername(String username);
}

package com.solo.toyboard.repository;

import com.solo.toyboard.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsername(String username);
    boolean existsByUsername(String username);

}

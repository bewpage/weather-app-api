package com.weatherapp.dao;

import com.weatherapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
  User findByUsernameOrEmail(String username, String email);

  User findByUsername(String username);

  User findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}

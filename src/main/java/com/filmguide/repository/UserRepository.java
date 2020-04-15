package com.filmguide.repository;

import com.filmguide.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);
    public Optional<User> findByEmail(String email);

}

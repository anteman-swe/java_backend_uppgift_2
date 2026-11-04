package com.example.helpdesk.repository;

import com.example.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    java.util.List<User> findByRole(User.Role role);

    java.util.List<User> findByActive(boolean active);

    java.util.List<User> findByRoleAndActive(User.Role role, boolean active);
}

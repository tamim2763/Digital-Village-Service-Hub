package com.digitalvillage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalvillage.entity.User;

/**
 * Sample repository to demonstrate Spring Data JPA structure.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findTopByOrderByCreatedAtDesc();
}

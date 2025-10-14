package com.back.motionit.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.motionit.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

package com.back.motionit.domain.user.repository;

import com.back.motionit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByKakaoId(Long kakaoId);

	boolean existsByEmail(String email);
}

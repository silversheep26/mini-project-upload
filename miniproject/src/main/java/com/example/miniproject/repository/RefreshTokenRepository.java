package com.example.miniproject.repository;

import com.example.miniproject.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserid(String userid);
    void deleteByUserid(String userid);
}

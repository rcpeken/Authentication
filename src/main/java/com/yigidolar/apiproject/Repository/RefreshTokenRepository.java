package com.yigidolar.apiproject.Repository;

import com.yigidolar.apiproject.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUsername(String username);
}

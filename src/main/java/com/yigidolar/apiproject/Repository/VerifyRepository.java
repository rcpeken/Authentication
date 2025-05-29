package com.yigidolar.apiproject.Repository;

import com.yigidolar.apiproject.Entity.VerifyModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyRepository extends JpaRepository<VerifyModel, Long> {
    Optional<VerifyModel> findByEmailAndType(String email, int type);
}

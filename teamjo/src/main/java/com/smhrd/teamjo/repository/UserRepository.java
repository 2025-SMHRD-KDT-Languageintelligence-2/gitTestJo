package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {
    // String은 PK 타입(U_ID)을 의미함
    
    // 이메일 중복 확인
    boolean existsByEmail(String email);

    UserInfo findByEmailAndPassword(String email, String password);
}

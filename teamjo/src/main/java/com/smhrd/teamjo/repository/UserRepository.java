package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {
    
    // PK인 U_ID 기준으로 중복 확인
    boolean existsByUid(String uid); // JpaRepository 기본 제공이지만 명시적으로 작성 가능

    // 로그인용: U_ID와 비밀번호 일치 확인
    UserInfo findByUidAndPassword(String uid, String password);
}

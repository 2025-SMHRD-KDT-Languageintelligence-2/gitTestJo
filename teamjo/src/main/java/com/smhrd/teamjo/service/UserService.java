package com.smhrd.teamjo.service;

import java.util.Optional;

import com.smhrd.teamjo.dto.UserDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.WeightRecord;
import com.smhrd.teamjo.repository.UserRepository;
import com.smhrd.teamjo.repository.WeightRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeightRecordRepository weightRecordRepository;

    // 회원가입 처리 메서드
    public void join(UserDTO dto){
        System.out.println("회원가입 요청 들어옴: " + dto.getEmail());

        // 이메일을 U_ID로 설정
        String uid = dto.getEmail();

        // 현재 시간 기준 가입일 설정
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // UserInfo 엔티티로 변환
        UserInfo user = new UserInfo();
        user.setUid(uid);
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // 나중에 암호화 처리 진행 하기 ***
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());

        user.setJoinedAt(now); // 가입일시 기록
        user.setRole("USER");
        user.setLoginSrc("local");

        // DB 저장
        userRepository.save(user);
    }

    public UserInfo login(String email, String password){
        return userRepository.findByEmailAndPassword(email, password);
    }

    // 체중 기록 저장 및 USER_INFO 동기화
    public void saveWeight(String userId, double weight){
        //1. 체중 기록 저장
        WeightRecord record = new WeightRecord();
        record.setUserId(userId);
        record.setWeight(weight);
        record.setRecordedAt(LocalDateTime.now());
        weightRecordRepository.save(record);

        //2. USER_INFO 테이블의 U_WEIGHT도 업데이트
        Optional<UserInfo> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(user -> {
            user.setWeight(weight);
            userRepository.save(user);
        });
    }
}

package com.smhrd.teamjo.service;

import java.util.Optional;

import com.smhrd.teamjo.dto.UserDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.WeightRecord;
import com.smhrd.teamjo.repository.UserRepository;
import com.smhrd.teamjo.repository.WeightRecordRepository;

import jakarta.transaction.Transactional;

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

    // 회원가입 처리
    public void join(UserDTO dto){
        System.out.println("회원가입 요청 들어옴: " + dto.getEmail());

        String uid = dto.getEmail();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        UserInfo user = new UserInfo();
        user.setUid(uid);
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());

        user.setJoinedAt(now);
        user.setRole("USER");
        user.setLoginSrc("local");

        userRepository.save(user);
    }

    public UserInfo login(String email, String password){
        return userRepository.findByEmailAndPassword(email, password);
    }

    // 체중 기록 저장 + USER_INFO 업데이트
    public void saveWeight(String userId, double weight){
        WeightRecord record = new WeightRecord();
        record.setUserId(userId);
        record.setWeight(weight);
        record.setRecordedAt(LocalDateTime.now());
        weightRecordRepository.save(record);

        userRepository.findById(userId).ifPresent(user -> {
            user.setWeight(weight);
            userRepository.save(user);
        });
    }

    // 🔄 기존 메서드는 유지하면서, 아래 새 메서드 추가
    @Transactional
    public void updateRecomCal(String uid, double targetCalories) {
        userRepository.findById(uid).ifPresent(user -> {
            user.setRecomCal((int)Math.round(targetCalories));
            userRepository.save(user);
        });
    }

    // 🔄 식사 횟수와 시간대까지 함께 저장
    @Transactional
    public void updateCalorieAndMealInfo(String uid, int recomCal, int mealCount, String mealTimes) {
        userRepository.findById(uid).ifPresent(user -> {
            user.setRecomCal(recomCal);
            user.setMealCount(mealCount);     // 하루 식사 횟수
            user.setMealTimes(mealTimes);     // 아침,점심,저녁 형식
            userRepository.save(user);
        });
    }
}

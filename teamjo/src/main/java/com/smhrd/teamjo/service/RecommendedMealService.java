package com.smhrd.teamjo.service;

import com.smhrd.teamjo.entity.RecommendedMeal;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.RecommendedMealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendedMealService {

    private final RecommendedMealRepository recommendedMealRepository;

    // 사용자 객체를 받아서 uid만 추출해 저장하는 방식
    @Transactional
    public void saveNewRecommendedMeals(UserInfo user, List<Map<String, Object>> meals) {
        // 1. 기존 식단 삭제
        recommendedMealRepository.deleteAllByUserId(user.getUid());

        // 2. 새로운 식단 구성
        List<RecommendedMeal> newMeals = meals.stream().map(meal -> {
            int day = ((Number) meal.get("day")).intValue();
            String time = (String) meal.get("time");
            String rice = (String) meal.get("rice");
            String soup = (String) meal.get("soup");
            String side = (String) meal.get("side");
            double totalCal = ((Number) meal.get("totalCal")).doubleValue();

            LocalDate mealDate = LocalDate.now().plusDays(day - 1);
            String weekday = mealDate.getDayOfWeek().name().substring(0, 3); // MON, TUE 등

            RecommendedMeal rm = new RecommendedMeal();
            rm.setUserId(user.getUid()); // ✅ 수정된 부분
            rm.setMealDate(mealDate);
            rm.setWeekday(weekday);
            rm.setTime(time);
            rm.setRice(rice);
            rm.setSoup(soup);
            rm.setSide(side);
            rm.setTotalCalories(totalCal);
            rm.setCreatedAt(LocalDateTime.now());

            return rm;
        }).toList();

        // 3. DB 저장
        recommendedMealRepository.saveAll(newMeals);
    }
}

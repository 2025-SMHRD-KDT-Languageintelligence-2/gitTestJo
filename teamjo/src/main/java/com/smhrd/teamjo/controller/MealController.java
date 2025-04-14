// src/main/java/com/smhrd/teamjo/controller/MealController.java
package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.RecommendedMealDTO;
import com.smhrd.teamjo.entity.RecommendedMeal;
import com.smhrd.teamjo.repository.RecommendedMealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MealController {

    private final RecommendedMealRepository repository;

    @Transactional  // 🔥 이 줄 추가!
    @PostMapping("/save-recommend")
    public String saveRecommend(@RequestBody List<RecommendedMealDTO> meals) {
        if (meals.isEmpty()) return "no data";

        String userId = meals.get(0).getUserId();
        repository.deleteByUserId(userId); // 기존 식단 삭제

        LocalDate baseDate = LocalDate.now();

        for (RecommendedMealDTO dto : meals) {
            RecommendedMeal meal = new RecommendedMeal();
            meal.setUserId(dto.getUserId());
            meal.setTime(dto.getTime());
            meal.setRice(dto.getRice());
            meal.setSoup(dto.getSoup());
            meal.setSide(dto.getSide());
            meal.setTotalCalories(dto.getTotalCal());
            meal.setSimilarity(dto.getSimilarity()); // 유사도도 넣어줄 수 있다면

            LocalDate mealDate = baseDate.plusDays(dto.getDay() - 1);
            meal.setMealDate(mealDate);
            meal.setWeekday(mealDate.getDayOfWeek().toString().substring(0, 3));

            repository.save(meal);
        }

        return "ok";
    }
}

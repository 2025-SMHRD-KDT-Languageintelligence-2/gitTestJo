package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.RecommendedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RecommendedMealRepository extends JpaRepository<RecommendedMeal, Long> {

    List<RecommendedMeal> findByUserId(String userId);

    List<RecommendedMeal> findByUserIdAndTime(String userId, String time);

    List<RecommendedMeal> findByUserIdAndMealDate(String userId, LocalDate date);

    List<RecommendedMeal> findByUserIdAndMealDateAndTime(String userId, LocalDate date, String time);

    List<RecommendedMeal> findByUserIdAndMealDateBetween(String userId, LocalDate start, LocalDate end);

    void deleteByUserIdAndTime(String userId, String time);

}

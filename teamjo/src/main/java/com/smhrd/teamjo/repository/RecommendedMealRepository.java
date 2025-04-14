package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.RecommendedMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedMealRepository extends JpaRepository<RecommendedMeal, Long> {

    List<RecommendedMeal> findByUserId(String userId);

    void deleteByUserId(String userId);  // 🔥 이거 추가
}

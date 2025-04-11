package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.RecommendedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecommendedMealRepository extends JpaRepository<RecommendedMeal, Long> {

    // 사용자 전체 식단 조회
    List<RecommendedMeal> findByUserId(String userId);

    List<RecommendedMeal> findByUserIdAndTime(String userId, String time);

    List<RecommendedMeal> findByUserIdAndMealDate(String userId, LocalDate date);

    List<RecommendedMeal> findByUserIdAndMealDateAndTime(String userId, LocalDate date, String time);

    List<RecommendedMeal> findByUserIdAndMealDateBetween(String userId, LocalDate start, LocalDate end);

    // 특정 시간대의 식단만 삭제
    void deleteByUserIdAndTime(String userId, String time);

    // 🔥 사용자 전체 식단 삭제 (재추천 시 사용됨)
    @Modifying
    @Query("DELETE FROM RecommendedMeal r WHERE r.userId = :userId")  // 🔁 수정됨: r.user.id → r.userId
    void deleteAllByUserId(@Param("userId") String userId);
}

package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.FoodReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodReviewRepository extends JpaRepository<FoodReview, Integer> {

    // 특정 음식에 대한 모든 리뷰 조회
    List<FoodReview> findByFoodId(String foodId);
}
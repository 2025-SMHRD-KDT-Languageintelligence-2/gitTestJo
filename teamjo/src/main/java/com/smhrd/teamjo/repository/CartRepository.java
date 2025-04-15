package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.Cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(String userId);

    // ✅ 특정 유저의 특정 음식 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.foodId = :foodId")
    void deleteByUserIdAndFoodId(@Param("userId") String userId, @Param("foodId") String foodId);

    // ✅ 특정 유저의 장바구니 전체 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
}

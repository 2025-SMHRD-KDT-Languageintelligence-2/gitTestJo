package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.OrderInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderInfo, Long> {
    List<OrderInfo> findByUserId(String userId);
    List<OrderInfo> findByUserIdOrderByCreatedAtDesc(String userId);
}

package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.WeightRecord;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {
    List<WeightRecord> findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(String userId, LocalDateTime after);
}


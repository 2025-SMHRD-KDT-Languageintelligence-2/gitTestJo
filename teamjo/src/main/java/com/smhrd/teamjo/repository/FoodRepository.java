// src/main/java/com/smhrd/teamjo/repository/FoodRepository.java
package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.entity.FoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodRepository extends JpaRepository<FoodInfo, String> {
    List<FoodInfo> findByType(String type);
}

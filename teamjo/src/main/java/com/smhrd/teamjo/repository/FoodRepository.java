package com.smhrd.teamjo.repository;

import com.smhrd.teamjo.dto.FoodWithScore;
import com.smhrd.teamjo.entity.FoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<FoodInfo, String> {

    List<FoodInfo> findByType(String type);
    
    Optional<FoodInfo> findByName(String name);

    // 기존 entity 반환은 유지하되, DTO로 평점까지 가져오는 쿼리 추가
    @Query(value = """
        SELECT
            fi.FOOD_ID as foodId,
            fi.F_NAME as name,
            fi.F_TYPE as type,
            fi.F_ENERGY as energy,
            fi.F_PROTEIN as protein,
            fi.F_PROVINCE as province,
            fi.F_IMG as img,
            AVG(fr.F_SCORE) as avgScore
        FROM FOOD_INFO fi
        JOIN FOOD_REVIEW fr ON fi.FOOD_ID = fr.FOOD_ID
        WHERE fi.F_TYPE = :type
        GROUP BY fi.FOOD_ID
        ORDER BY avgScore DESC
        LIMIT 3
    """, nativeQuery = true)
    List<FoodWithScore> findTop3ByTypeWithScore(@Param("type") String type);

}
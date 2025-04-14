// src/main/java/com/smhrd/teamjo/entity/FoodInfo.java
package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FOOD_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodInfo {

    @Id
    @Column(name = "FOOD_ID")
    private String foodId;

    @Column(name = "F_NAME")
    private String name;

    @Column(name = "F_TYPE")
    private String type;

    @Column(name = "F_ENERGY")
    private Double energy;

    @Column(name = "F_PROTEIN")
    private Double protein;

    @Column(name = "F_PROVINCE")
    private Double province;  // 지방

    @Column(name = "F_CARBO")
    private Double carbo;     // 탄수화물

    @Column(name = "F_SUGAR")
    private Double sugar;     // 당류

    @Column(name = "F_SODIUM")
    private Double sodium;    // 나트륨

    @Column(name = "F_COL")
    private Double col;       // 콜레스테롤

    @Column(name = "F_SAT")
    private Double sat;       // 포화지방

    @Column(name = "F_TRANS")
    private Double trans;     // 트랜스지방

    @Column(name = "F_PRICE")
    private Integer price;    // 가격 (선택 사항)

    @Column(name = "F_NUTR")
    private String nutr;      // 영양소 요약 정보 (선택 사항)

    @Column(name = "F_IMG")
    private String img;

    @Column(name = "F_TAGS")
    private String tags;
}

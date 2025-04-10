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

    @Column(name = "F_IMG")
    private String img;
}

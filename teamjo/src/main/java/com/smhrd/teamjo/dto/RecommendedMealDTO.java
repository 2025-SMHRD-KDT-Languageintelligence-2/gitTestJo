// src/main/java/com/smhrd/teamjo/dto/RecommendedMealDTO.java
package com.smhrd.teamjo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendedMealDTO {
    private String userId;
    private String time;
    private String rice;
    private String soup;
    private String side;
    private double totalCal;
    private int day; // 1~7일차 (요일 계산용)
    private Double similarity;
}

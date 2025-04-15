package com.smhrd.teamjo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDTO {
    private String foodId; // 사용자가 담은 식품 ID
    private Long mealId;
}

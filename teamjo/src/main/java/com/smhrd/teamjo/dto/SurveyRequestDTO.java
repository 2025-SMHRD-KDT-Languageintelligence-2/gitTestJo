package com.smhrd.teamjo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SurveyRequestDTO {

    // 0. 선호하는 밥 종류 (복수 선택)
    private List<String> preferredRiceTypes;

    // 1. 선호하는 조리 방식
    private List<String> cookingStyles;

    // 2. 피하고 싶은 음식 특성
    private List<String> dislikedFeatures;

    // 3. 기피하는 재료 (쉼표로 입력받음)
    private String dislikedIngredients;

    // 4. 당류 민감도 (무관심 / 중간 / 고)
    private String sugarSensitivity;

    // 5. 선호하는 재료
    private List<String> preferredIngredients;

    // 6. 매운맛 선호 여부 (yes / no)
    private String spicyPreference;

    // 7. 칼로리 분배 (식사 시간에 따라 일부 값만 들어올 수 있음)
    private Integer calRatioMorning;
    private Integer calRatioLunch;
    private Integer calRatioDinner;
}

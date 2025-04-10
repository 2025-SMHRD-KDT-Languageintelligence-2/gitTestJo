package com.smhrd.teamjo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SurveyRequestDTO {

    private String dietGoal; // 식단 목표 (diet, protein, balance, normal)

    private List<String> cookingStyles; // 조리 방식 (국물요리, 볶음요리 등)

    private List<String> dislikedFeatures; // 피하고 싶은 특성 (매운맛, 발효국 등)

    private String dislikedIngredients; // 기피 재료 (텍스트로 입력, 쉼표 구분)

    private String sugarSensitivity; // 당류 민감도 (무관심, 중간, 고)

    private List<String> preferredIngredients; // 선호 재료 (닭고기, 두부 등)

    private int spiceLevel; // 매운맛 선호도 (0~100)

    private int calRatioMorning; // 아침 비율 (%)
    private int calRatioLunch;   // 점심 비율 (%)
    private int calRatioDinner;  // 저녁 비율 (%)
}
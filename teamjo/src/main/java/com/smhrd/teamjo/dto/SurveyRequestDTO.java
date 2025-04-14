package com.smhrd.teamjo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SurveyRequestDTO {
    private List<String> preferredRiceTypes;
    private List<String> userTags;        // cookingStyles + preferredIngredients + 영양 요소
    private List<String> dislikedTags;    // 기피 재료 + 조리 특성
    private Integer calRatioMorning;
    private Integer calRatioLunch;
    private Integer calRatioDinner;
}
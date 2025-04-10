package com.smhrd.teamjo.util;

import com.smhrd.teamjo.util.MealCombination;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    // 예시: CSV 또는 DB에서 가져온 식단 후보 목록 (임시 하드코딩)
    private List<MealCombination> sampleMeals = List.of(
        new MealCombination("현미밥", "미역국", "돼지고기볶음", 850),
        new MealCombination("흰쌀밥", "된장국", "계란말이", 890),
        new MealCombination("잡곡밥", "맑은콩나물국", "두부조림", 820)
    );

    // 키워드 기반으로 가장 유사한 식단 하나 추천
    public MealCombination recommend(List<String> riceKeywords,
                                     List<String> soupKeywords,
                                     List<String> sideKeywords,
                                     int maxCalories) {

        MealCombination best = null;
        int maxScore = -1;

        for (MealCombination meal : sampleMeals) {
            if (meal.getTotalCalories() > maxCalories) continue;

            int score = 0;
            score += matchScore(meal.getRice(), riceKeywords);
            score += matchScore(meal.getSoup(), soupKeywords);
            score += matchScore(meal.getSide(), sideKeywords);

            if (score > maxScore) {
                maxScore = score;
                best = meal;
            }
        }

        return best;
    }

    // 단순 키워드 포함 여부로 점수 계산
    private int matchScore(String target, List<String> keywords) {
        int score = 0;
        for (String kw : keywords) {
            if (target.contains(kw)) score++;
        }
        return score;
    }

    // 설문 결과 Map에서 키워드 리스트 추출 (단순 split 처리)
    public List<String> extractKeywords(Map<String, String> formData) {
        List<String> keywords = new ArrayList<>();
        for (String value : formData.values()) {
            if (value != null) {
                keywords.addAll(Arrays.asList(value.split(",| |/")));
            }
        }
        return keywords.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
} 

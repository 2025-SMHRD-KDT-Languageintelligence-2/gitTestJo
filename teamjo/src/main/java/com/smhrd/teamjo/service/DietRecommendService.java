package com.smhrd.teamjo.service;

import com.smhrd.teamjo.dto.SurveyRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietRecommendService {

    private final List<String> allTags = Arrays.asList(
        "고단백", "저칼로리", "중단백", "중간칼로리", "국물요리", "볶음요리", "조림요리",
        "저당", "중간당", "매운맛", "발효국", "고지방",
        "돼지고기", "닭고기", "두부", "계란", "버섯", "김치", "청국장", "구이", "곤약"
    );

    public List<String> extractUserTags(SurveyRequestDTO survey) {
        List<String> tags = new ArrayList<>();
        switch (survey.getDietGoal()) {
            case "protein" -> tags.add("고단백");
            case "diet" -> tags.add("저칼로리");
            case "balance" -> tags.addAll(List.of("중단백", "중간칼로리"));
            case "normal" -> tags.add("중간칼로리");
        }
        if (survey.getCookingStyles() != null)
            tags.addAll(survey.getCookingStyles());
        switch (survey.getSugarSensitivity()) {
            case "고" -> tags.add("저당");
            case "중간" -> tags.add("중간당");
        }
        if (survey.getPreferredIngredients() != null)
            tags.addAll(survey.getPreferredIngredients());
        if (survey.getSpiceLevel() <= 30)
            tags.add("매운맛");
        return tags.stream().distinct().collect(Collectors.toList());
    }

    public int[] toVector(List<String> tags) {
        int[] vector = new int[allTags.size()];
        for (int i = 0; i < allTags.size(); i++) {
            vector[i] = tags.contains(allTags.get(i)) ? 1 : 0;
        }
        return vector;
    }

    public double cosineSimilarity(int[] vec1, int[] vec2) {
        int dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return normA != 0 && normB != 0 ? dot / (Math.sqrt(normA) * Math.sqrt(normB)) : 0.0;
    }

    // 샘플용 음식 클래스 (실제론 엔티티에서 가져오겠지만 구조 확인용)
    public static class Food {
        public String name;
        public String type; // 밥류 / 국류 / 반찬
        public double cal;
        public List<String> tags;

        public Food(String name, String type, double cal, List<String> tags) {
            this.name = name;
            this.type = type;
            this.cal = cal;
            this.tags = tags;
        }
    }

    // 사용자 태그 기반 식단 7일치 추천 (밥+국+반찬 조합)
    public List<Map<String, Object>> recommendWeeklyMeals(SurveyRequestDTO survey, List<Food> foodList, double dailyCal) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 분배된 식사별 목표 칼로리
        Map<String, Double> targetCals = Map.of(
            "morning", dailyCal * survey.getCalRatioMorning() / 100.0,
            "lunch", dailyCal * survey.getCalRatioLunch() / 100.0,
            "dinner", dailyCal * survey.getCalRatioDinner() / 100.0
        );

        List<String> userTags = extractUserTags(survey);
        int[] userVector = toVector(userTags);

        // 음식 분류
        List<Food> riceList = foodList.stream().filter(f -> f.type.equals("밥류")).toList();
        List<Food> soupList = foodList.stream().filter(f -> f.type.equals("국류")).toList();
        List<Food> sideList = foodList.stream().filter(f -> f.type.equals("반찬")).toList();

        Random rand = new Random();
        String[] times = {"morning", "lunch", "dinner"};

        for (int day = 1; day <= 7; day++) {
            for (String time : times) {
                for (int trial = 0; trial < 300; trial++) {
                    Food rice = riceList.get(rand.nextInt(riceList.size()));
                    Food soup = soupList.get(rand.nextInt(soupList.size()));
                    Food side = sideList.get(rand.nextInt(sideList.size()));

                    double total = rice.cal + soup.cal + side.cal;
                    double target = targetCals.get(time);
                    if (Math.abs(total - target) <= 50) {
                        Map<String, Object> meal = new HashMap<>();
                        meal.put("day", day);
                        meal.put("time", time);
                        meal.put("rice", rice.name);
                        meal.put("soup", soup.name);
                        meal.put("side", side.name);
                        meal.put("totalCal", total);
                        result.add(meal);
                        break;
                    }
                }
            }
        }

        return result;
    }
}
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

        if (survey.getCookingStyles() != null)
            tags.addAll(survey.getCookingStyles());

        switch (survey.getSugarSensitivity()) {
            case "고" -> tags.add("저당");
            case "중간" -> tags.add("중간당");
        }

        if (survey.getPreferredIngredients() != null)
            tags.addAll(survey.getPreferredIngredients());

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

    public List<Map<String, Object>> recommendWeeklyMeals(SurveyRequestDTO survey, List<Food> foodList, double dailyCal) {
        List<Map<String, Object>> result = new ArrayList<>();

        // ✅ 기본값 처리
        if (survey.getPreferredRiceTypes() == null || survey.getPreferredRiceTypes().isEmpty()) {
            survey.setPreferredRiceTypes(List.of("현미밥", "잡곡밥"));
        }
        if (survey.getDislikedFeatures() == null) {
            survey.setDislikedFeatures(Collections.emptyList());
        }
        if (survey.getPreferredIngredients() == null) {
            survey.setPreferredIngredients(Collections.emptyList());
        }
        if (survey.getSpicyPreference() == null) {
            survey.setSpicyPreference("no");
        }
        if (survey.getSugarSensitivity() == null) {
            survey.setSugarSensitivity("무관심");
        }
        if (survey.getCalRatioMorning() == null) survey.setCalRatioMorning(33);
        if (survey.getCalRatioLunch() == null) survey.setCalRatioLunch(33);
        if (survey.getCalRatioDinner() == null) survey.setCalRatioDinner(34);

        Map<String, Double> targetCals = new HashMap<>();
        targetCals.put("morning", dailyCal * survey.getCalRatioMorning() / 100.0);
        targetCals.put("lunch", dailyCal * survey.getCalRatioLunch() / 100.0);
        targetCals.put("dinner", dailyCal * survey.getCalRatioDinner() / 100.0);

        List<String> userTags = extractUserTags(survey);
        int[] userVector = toVector(userTags);

        List<Food> riceList = foodList.stream()
            .filter(f -> f.type.equals("밥류"))
            .filter(f -> survey.getPreferredRiceTypes().contains(f.name))
            .toList();

        List<Food> soupList = foodList.stream()
            .filter(f -> f.type.equals("국류"))
            .filter(f -> isAllowedFood(f, survey))
            .toList();

        List<Food> sideList = foodList.stream()
            .filter(f -> f.type.equals("반찬"))
            .filter(f -> isAllowedFood(f, survey))
            .toList();

        System.out.println("🍚 밥 후보: " + riceList.size());
        System.out.println("🍲 국 후보: " + soupList.size());
        System.out.println("🥗 반찬 후보: " + sideList.size());

        if (riceList.isEmpty() || soupList.isEmpty() || sideList.isEmpty()) {
            System.out.println("⚠️ 필터링 조건이 너무 엄격해서 식단 생성 불가");
            return result;
        }

        Random rand = new Random();
        for (int day = 1; day <= 7; day++) {
            for (String time : targetCals.keySet()) {
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

    private boolean isAllowedFood(Food food, SurveyRequestDTO survey) {
        List<String> tags = food.tags;

        if ("no".equals(survey.getSpicyPreference()) && tags.contains("매운맛")) return false;

        if (survey.getDislikedFeatures() != null) {
            for (String tag : survey.getDislikedFeatures()) {
                if (tags.contains(tag)) return false;
            }
        }

        if (survey.getDislikedIngredients() != null && !survey.getDislikedIngredients().isBlank()) {
            List<String> ingrs = Arrays.stream(survey.getDislikedIngredients().split(","))
                .map(String::trim).toList();
            for (String ing : ingrs) {
                if (tags.contains(ing)) return false;
            }
        }

        return true;
    }
}

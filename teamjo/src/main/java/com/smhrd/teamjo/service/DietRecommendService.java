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
        "가지", "감자", "고구마", "고단백", "고당", "고지방", "고칼로리", "곤약", "국물요리",
        "굴", "김치", "낙지", "느타리", "닭", "닭고기", "당근", "돼지", "돼지고기",
        "두부", "매운맛", "멸치", "무", "문어", "미역", "발효국", "버섯", "볶음요리",
        "브로콜리", "새우", "소고기", "시금치", "애호박", "양배추", "양파", "연근", "오리고기",
        "오이", "오징어", "우거지", "우엉", "저단백", "저당", "저지방", "저칼로리", "조림요리",
        "중간당", "중간칼로리", "중단백", "중지방", "참치", "치즈", "콩", "콩나물", "파", "표고", "호박", "홍합"
    );

    public static class Food {
        public String name;
        public String type;
        public double cal;
        public List<String> tags;

        public Food(String name, String type, double cal, List<String> tags) {
            this.name = name;
            this.type = type;
            this.cal = cal;
            this.tags = tags;
        }
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

    private boolean isAllowedFood(Food food, SurveyRequestDTO survey) {
        if (survey.getDislikedTags() == null) return true;
        for (String tag : survey.getDislikedTags()) {
            if (food.tags.contains(tag)) return false;
        }
        return true;
    }

    public List<Map<String, Object>> recommendWeeklyMeals(SurveyRequestDTO survey, List<Food> foodList, double dailyCal) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (survey.getPreferredRiceTypes() == null || survey.getPreferredRiceTypes().isEmpty())
            survey.setPreferredRiceTypes(List.of("현미밥", "잡곡밥"));
        if (survey.getUserTags() == null)
            survey.setUserTags(new ArrayList<>());
        if (survey.getDislikedTags() == null)
            survey.setDislikedTags(new ArrayList<>());
        if (survey.getCalRatioMorning() == null) survey.setCalRatioMorning(30);
        if (survey.getCalRatioLunch() == null) survey.setCalRatioLunch(40);
        if (survey.getCalRatioDinner() == null) survey.setCalRatioDinner(30);

        Map<String, Double> targetCals = new HashMap<>();
        targetCals.put("morning", dailyCal * survey.getCalRatioMorning() / 100.0);
        targetCals.put("lunch", dailyCal * survey.getCalRatioLunch() / 100.0);
        targetCals.put("dinner", dailyCal * survey.getCalRatioDinner() / 100.0);

        int[] userVector = toVector(survey.getUserTags());

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

        if (riceList.isEmpty() || soupList.isEmpty() || sideList.isEmpty()) return result;

        Random rand = new Random();
        for (int day = 1; day <= 7; day++) {
            for (String time : targetCals.keySet()) {
                double target = targetCals.get(time);
                double bestScore = -1;
                Map<String, Object> bestMeal = null;

                for (int i = 0; i < 300; i++) {
                    Food rice = riceList.get(rand.nextInt(riceList.size()));
                    Food soup = soupList.get(rand.nextInt(soupList.size()));
                    Food side = sideList.get(rand.nextInt(sideList.size()));

                    double totalCal = rice.cal + soup.cal + side.cal;
                    if (Math.abs(totalCal - target) > 50) continue;

                    List<String> combinedTags = new ArrayList<>();
                    combinedTags.addAll(soup.tags);
                    combinedTags.addAll(side.tags);

                    int[] foodVector = toVector(combinedTags);
                    double similarity = cosineSimilarity(userVector, foodVector);

                    // ✅ 유사도 로그 출력
                    System.out.println("추천 조합: " + rice.name + " + " + soup.name + " + " + side.name);
                    System.out.println("   🔁 유사도: " + similarity);
                    System.out.println("   🔎 총칼로리: " + totalCal + " / 목표: " + target);

                    if (similarity > bestScore) {
                        bestScore = similarity;
                        bestMeal = Map.of(
                            "day", day,
                            "time", time,
                            "rice", rice.name,
                            "soup", soup.name,
                            "side", side.name,
                            "totalCal", totalCal,
                            "similarity", similarity
                        );
                    }
                }

                if (bestMeal != null) {
                    result.add(bestMeal);
                }
            }
        }

        return result;
    }
}

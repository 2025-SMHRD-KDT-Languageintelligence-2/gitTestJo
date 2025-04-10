package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.SurveyRequestDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.FoodInfo;
import com.smhrd.teamjo.entity.RecommendedMeal;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.repository.RecommendedMealRepository;
import com.smhrd.teamjo.service.DietRecommendService;
import com.smhrd.teamjo.service.DietRecommendService.Food;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SurveyController {

    private final DietRecommendService recommendService;
    private final FoodRepository foodRepository;
    private final RecommendedMealRepository mealRepository;

    // 1. 설문 제출 → 세션에 설문 저장 후 로딩 페이지로 이동
    @PostMapping("/submit-survey")
    public String handleSurveySubmit(@ModelAttribute SurveyRequestDTO survey, HttpSession session) {
        session.setAttribute("survey", survey);
        return "redirect:/diet/loading";
    }

    // 2. 로딩 페이지 반환
    @GetMapping("/diet/loading")
    public String showLoadingPage() {
        return "loading"; // templates/loading.html
    }

    // 3. 추천 알고리즘 실행 (fetch로 호출됨)
    @PostMapping("/process-algorithm")
    @ResponseBody
    public String runAlgorithm(HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        SurveyRequestDTO survey = (SurveyRequestDTO) session.getAttribute("survey");

        if (loginUser == null || survey == null) {
            return "error";
        }

        String userId = loginUser.getUid();
        int recomCal = loginUser.getRecomCal();

        // FOOD_INFO → Food DTO로 변환
        List<Food> foodList = foodRepository.findAll().stream()
                .map(e -> new Food(
                        e.getName(),
                        e.getType(),
                        e.getEnergy(),
                        List.of() // 태그는 추후 필요 시 확장
                )).collect(Collectors.toList());

        // 알고리즘 실행
        List<Map<String, Object>> weeklyMeals = recommendService.recommendWeeklyMeals(survey, foodList, recomCal);

        // DB 저장
        LocalDate startDate = LocalDate.now();
        for (Map<String, Object> meal : weeklyMeals) {
            int dayOffset = (int) meal.get("day") - 1;
            LocalDate mealDate = startDate.plusDays(dayOffset);

            RecommendedMeal rm = new RecommendedMeal();
            rm.setUserId(userId);
            rm.setTime((String) meal.get("time"));
            rm.setRice((String) meal.get("rice"));
            rm.setSoup((String) meal.get("soup"));
            rm.setSide((String) meal.get("side"));
            rm.setTotalCalories((Double) meal.get("totalCal"));
            rm.setMealDate(mealDate);
            rm.setWeekday(mealDate.getDayOfWeek().name().substring(0, 3));

            mealRepository.save(rm);
        }

        return "ok";
    }
}

package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.SurveyRequestDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.service.DietRecommendService;
import com.smhrd.teamjo.service.DietRecommendService.Food;
import com.smhrd.teamjo.service.RecommendedMealService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SurveyController {

    private final DietRecommendService recommendService;
    private final RecommendedMealService recommendedMealService;
    private final FoodRepository foodRepository;

    // ✅ 0. 설문 페이지 열기 (식사시간 기반으로 칼로리 분배 필드 조절)
    @GetMapping("/survey")
    public String showSurveyForm(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null) {
            model.addAttribute("mealTimes", loginUser.getMealTimes()); // 예: "아침,점심"
        }

        return "preference-form"; // templates/preference-form.html
    }

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

        int recomCal = loginUser.getRecomCal();

        // FOOD_INFO → Food DTO로 변환
        List<Food> foodList = foodRepository.findAll().stream()
                .map(e -> new Food(
                        e.getName(),
                        e.getType(),
                        e.getEnergy(),
                        List.of() // 태그는 추후 확장 가능
                )).collect(Collectors.toList());

        // 추천 알고리즘 실행
        List<Map<String, Object>> weeklyMeals = recommendService.recommendWeeklyMeals(survey, foodList, recomCal);

        // 기존 식단 삭제 후 새 식단 저장
        recommendedMealService.saveNewRecommendedMeals(loginUser, weeklyMeals);

        return "ok";
    }
}

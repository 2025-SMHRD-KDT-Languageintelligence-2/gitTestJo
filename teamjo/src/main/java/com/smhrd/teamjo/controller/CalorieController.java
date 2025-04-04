package com.smhrd.teamjo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;

@Controller
public class CalorieController {

    // 🔹 POST: 처방 계산 후 리다이렉트
    @PostMapping("/calorie-result")
    public String calculateResult(
            @RequestParam double height,
            @RequestParam double currentWeight,
            @RequestParam double goalWeight,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam int duration,
            @RequestParam String unit,
            @RequestParam String activity,
            @RequestParam String gender,
            RedirectAttributes rttr
    ) {
        // 1. 나이 계산
        LocalDate birth = LocalDate.of(year, month, day);
        int age = Period.between(birth, LocalDate.now()).getYears();

        // 2. BMR 계산
        double bmr = gender.equals("male")
                ? 10 * currentWeight + 6.25 * height - 5 * age + 5
                : 10 * currentWeight + 6.25 * height - 5 * age - 161;

        // 3. 활동 지수로 TDEE 계산
        double activityFactor = switch (activity) {
            case "1" -> 1.2;
            case "2" -> 1.375;
            case "3" -> 1.55;
            case "4" -> 1.725;
            case "5" -> 1.9;
            default -> 1.2;
        };
        double tdee = bmr * activityFactor;

        // 4. 감량 목표
        double loseWeight = Math.max(0, currentWeight - goalWeight);

        // 5. 감량 기간 (일)
        int totalDays = unit.equals("month") ? duration * 30 : duration;

        // 6. 하루 감량 칼로리
        double kcalPerKg = 7700;
        double totalDeficit = loseWeight * kcalPerKg;
        double dailyDeficit = totalDays > 0 ? totalDeficit / totalDays : 0;

        // 7. 권장 섭취 칼로리 (최소 1200 kcal 보장)
        double targetCalories = Math.max(tdee - dailyDeficit, 1200);

        // 8. FlashAttributes로 값 전달
        rttr.addFlashAttribute("bmr", (int) bmr);
        rttr.addFlashAttribute("tdee", (int) tdee);
        rttr.addFlashAttribute("dailyDeficit", (int) dailyDeficit);
        rttr.addFlashAttribute("targetCalories", (int) targetCalories);
        rttr.addFlashAttribute("duration", duration);
        rttr.addFlashAttribute("unit", unit);
        rttr.addFlashAttribute("loseWeight", loseWeight);

        return "redirect:/calorie-result";
    }

    // 🔹 GET: Flash 값이 있으면 결과 페이지로 이동, 없으면 다시 입력 페이지로
    @GetMapping("/calorie-result")
    public String showResult(Model model) {
        if (!model.containsAttribute("bmr")) {
            return "redirect:/calorie";
        }
        return "calorie-result";
    }
}

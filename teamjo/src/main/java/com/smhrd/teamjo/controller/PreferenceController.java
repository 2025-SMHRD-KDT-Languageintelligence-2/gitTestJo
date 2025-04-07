package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.entity.*;
import com.smhrd.teamjo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class PreferenceController {

    @Autowired
    private RicePreferenceRepository riceRepo;

    @Autowired
    private SoupPreferenceRepository soupRepo;

    @Autowired
    private SidePreferenceRepository sideRepo;

    // 🍚 밥 설문 저장
    @PostMapping("/submitRiceSurvey")
    public String submitRiceSurvey(HttpSession session,
                                   @RequestParam("rice_type") String riceType,
                                   @RequestParam("stickiness") String stickiness,
                                   @RequestParam(value = "grains", required = false, defaultValue = "false") boolean grains,
                                   @RequestParam(value = "exclude_ingredients", required = false) String excludeIngredients,
                                   @RequestParam("diet_rice_pref") String dietRicePref) {

        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/";

        // 기존 설문 제거
        riceRepo.findByUserId(loginUser.getUid()).ifPresent(riceRepo::delete);

        RicePreference rice = new RicePreference();
        rice.setUserId(loginUser.getUid());
        rice.setRiceType(riceType);
        rice.setStickiness(stickiness);
        rice.setGrains(grains);
        rice.setExcludeIngredients(excludeIngredients != null ? excludeIngredients : "");
        rice.setDietRicePref(dietRicePref);
        rice.setCreatedAt(LocalDateTime.now());

        riceRepo.save(rice);
        System.out.println("🍚 밥 설문 저장 완료: " + rice);

        return "redirect:/soup-preference";
    }

    // 🍲 국 설문 저장
    @PostMapping("/submitSoupSurvey")
    public String submitSoupSurvey(HttpSession session,
                                   @RequestParam("soup_type") String soupType,
                                   @RequestParam(value = "soup_type_other", required = false) String soupTypeOther,
                                   @RequestParam("spicy_level") String spicyLevel,
                                   @RequestParam("soup_amount") String soupAmount,
                                   @RequestParam(value = "exclude_ingredients", required = false) String excludeIngredients,
                                   @RequestParam("diet_soup_pref") String dietSoupPref) {

        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/";

        if ("other".equals(soupType) && soupTypeOther != null && !soupTypeOther.isEmpty()) {
            soupType = soupTypeOther;
        }

        soupRepo.findByUserId(loginUser.getUid()).ifPresent(soupRepo::delete);

        SoupPreference soup = new SoupPreference();
        soup.setUserId(loginUser.getUid());
        soup.setSoupType(soupType);
        soup.setSpicyLevel(spicyLevel);
        soup.setSoupAmount(soupAmount);
        soup.setExcludeIngredients(excludeIngredients != null ? excludeIngredients : "");
        soup.setDietSoupPref(dietSoupPref);
        soup.setCreatedAt(LocalDateTime.now());

        soupRepo.save(soup);
        System.out.println("🍲 국 설문 저장 완료: " + soup);

        return "redirect:/side-preference";
    }

    // 🍽 반찬 설문 저장
    @PostMapping("/processing")
    public String submitSideSurvey(HttpSession session,
                                   @RequestParam("side_type") String sideType,
                                   @RequestParam(value = "side_type_other", required = false) String sideTypeOther,
                                   @RequestParam("side_flavor") String sideFlavor,
                                   @RequestParam(value = "exclude_ingredients", required = false) String excludeIngredients,
                                   @RequestParam("avoid_type") String avoidType,
                                   @RequestParam("cooking_method") String cookingMethod) {

        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/";

        if ("etc".equals(sideType) && sideTypeOther != null && !sideTypeOther.isEmpty()) {
            sideType = sideTypeOther;
        }

        sideRepo.findByUserId(loginUser.getUid()).ifPresent(sideRepo::delete);

        SidePreference side = new SidePreference();
        side.setUserId(loginUser.getUid());
        side.setSideType(sideType);
        side.setSideFlavor(sideFlavor);
        side.setExcludeIngredients(excludeIngredients != null ? excludeIngredients : "");
        side.setAvoidType(avoidType);
        side.setCookingMethod(cookingMethod);
        side.setCreatedAt(LocalDateTime.now());

        sideRepo.save(side);
        System.out.println("🍽 반찬 설문 저장 완료: " + side);

        return "processing";
    }
}

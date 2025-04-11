package com.smhrd.teamjo.controller;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smhrd.teamjo.dto.FoodWithScore;
import com.smhrd.teamjo.entity.FoodInfo;
import com.smhrd.teamjo.entity.RecommendedMeal;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.WeightRecord;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.repository.RecommendedMealRepository;
import com.smhrd.teamjo.repository.UserRepository;
import com.smhrd.teamjo.repository.WeightRecordRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendedMealRepository recommendedMealRepository;

    @Autowired
    private WeightRecordRepository weightRecordRepository;

    @GetMapping({"/", "/main"})
    public String mainPage(HttpSession session, Model model){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userName", loginUser.getName());
            model.addAttribute("userUid", loginUser.getUid());
            model.addAttribute("userProfileImg", loginUser.getProfile_img());
            model.addAttribute("user", loginUser);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "main";
    }

    @GetMapping("/start-diet")
    public String startDietFlow(HttpSession session){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if(loginUser == null || loginUser.getRecomCal() == 0){
            return "redirect:/calorie";
        }

        return "redirect:/preference-form";
    }

    @GetMapping("/preference-form")
    public String showPreferenceForm() {
        return "preference-form"; // templates/preference-form.html
    }

    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/";
        }

        Optional<UserInfo> userOpt = userRepository.findById(loginUser.getUid());
        if (userOpt.isPresent()) {
            UserInfo user = userOpt.get();
            session.setAttribute("loginUser", user);
            model.addAttribute("user", user);
            model.addAttribute("userName", user.getName());
            model.addAttribute("userAge", user.getAge());
            model.addAttribute("userSex", user.getSex());

            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            List<WeightRecord> records = weightRecordRepository
                    .findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(user.getUid(), oneMonthAgo);
            model.addAttribute("weightRecords", records);

            List<RecommendedMeal> meals = recommendedMealRepository.findByUserId(user.getUid());
            Map<String, List<String>> mealsByDay = new HashMap<>();

            for (String day : List.of("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")) {
                mealsByDay.put(day, Arrays.asList("정보 없음", "정보 없음", "정보 없음"));
            }

            for (RecommendedMeal meal : meals) {
                String day = meal.getWeekday();
                String combined = meal.getRice() + " + " + meal.getSoup() + " + " + meal.getSide();
                switch (meal.getTime()) {
                    case "morning" -> mealsByDay.get(day).set(0, combined);
                    case "lunch" -> mealsByDay.get(day).set(1, combined);
                    case "dinner" -> mealsByDay.get(day).set(2, combined);
                }
            }

            model.addAttribute("mealsByDay", mealsByDay);

            // 🔥 리뷰 기반 평점 포함된 인기 메뉴 (DTO)
            List<FoodWithScore> reviewRiceList = foodRepository.findTop3ByTypeWithScore("밥류");
            List<FoodWithScore> reviewSideList = foodRepository.findTop3ByTypeWithScore("반찬");
            List<FoodWithScore> reviewSoupList = foodRepository.findTop3ByTypeWithScore("국류");

            model.addAttribute("reviewRiceList", reviewRiceList);
            model.addAttribute("reviewSideList", reviewSideList);
            model.addAttribute("reviewSoupList", reviewSoupList);
        }

        return "mypage";
    }

    @GetMapping("/profile-edit")
    public String profileEditPage(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/";

        Optional<UserInfo> userOpt = userRepository.findById(loginUser.getUid());
        userOpt.ifPresent(user -> {
            model.addAttribute("nickname", user.getNick());
            model.addAttribute("height", user.getHeight());
            model.addAttribute("weight", user.getWeight());
            model.addAttribute("age", user.getAge());
            model.addAttribute("sex", user.getSex());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userUid", user.getUid());
        });

        model.addAttribute("user", loginUser);
        return "profile-edit";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("height") double height,
                                @RequestParam("weight") double weight,
                                @RequestParam("age") int age,
                                @RequestParam("sex") String sex,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null) {
            Optional<UserInfo> userOpt = userRepository.findById(loginUser.getUid());
            if (userOpt.isPresent()) {
                UserInfo user = userOpt.get();
                user.setNick(nickname);
                user.setHeight(height);
                user.setWeight(weight);
                user.setAge(age);
                user.setSex(sex);

                if (profileImage != null && !profileImage.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                        File dest = new File("C:/Users/smhrd/Desktop/gitTestJo-1/upload/" + fileName);
                        profileImage.transferTo(dest);
                        user.setProfile_img("/upload/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                userRepository.save(user);
                session.setAttribute("loginUser", user);
            }
        }

        return "redirect:/mypage";
    }

    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if(loginUser != null){
            Optional<UserInfo> userOpt = userRepository.findById(loginUser.getUid());
            userOpt.ifPresent(user -> {
                user.setProfile_img("/image/default_profile.png");
                userRepository.save(user);
                session.setAttribute("loginUser", user);
            });
        }
        return "redirect:/profile-edit";
    }

    @PostMapping("/recordWeight")
    public String recordWeight(@RequestParam("weight") double weight, HttpSession session){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null){
            WeightRecord newRecord = new WeightRecord();
            newRecord.setUserId(loginUser.getUid());
            newRecord.setWeight(weight);
            weightRecordRepository.save(newRecord);

            loginUser.setWeight(weight);
            userRepository.save(loginUser);
            session.setAttribute("loginUser", loginUser);
        }

        return "redirect:/mypage";
    }

    @GetMapping("/healty-map")
    public String showMapPage() {
        return "healty-map";
    }

    @Autowired
    private FoodRepository foodRepository;

    @GetMapping("/product-list")
    public String showProductList(Model model) {
        List<FoodInfo> foodList = foodRepository.findAll();
        model.addAttribute("foodList", foodList);
        return "product-list"; // templates/product-list.html
    }

    @GetMapping("/calorie")
    public String showCaloriePage() {
        return "calorie";
    }

    @GetMapping("/weight-chart")
    public String showWeightChart(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/";

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<WeightRecord> records = weightRecordRepository
                .findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(loginUser.getUid(), oneMonthAgo);

        model.addAttribute("weightRecords", records);
        return "weight-chart";
    }

    @GetMapping("/food-detail/{id}")
    public String showFoodDetail(@PathVariable("id") String id, Model model) {
        Optional<FoodInfo> foodOpt = foodRepository.findById(id);
        if (foodOpt.isPresent()) {
            model.addAttribute("food", foodOpt.get());
            return "food-detail";
        } else {
            return "redirect:/mypage";
        }
    }

    
}

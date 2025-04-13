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
import com.smhrd.teamjo.entity.FoodReview;
import com.smhrd.teamjo.entity.RecommendedMeal;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.WeightRecord;
import com.smhrd.teamjo.entity.FoodReview;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.repository.FoodReviewRepository;
import com.smhrd.teamjo.repository.RecommendedMealRepository;
import com.smhrd.teamjo.repository.UserRepository;
import com.smhrd.teamjo.repository.WeightRecordRepository;
import com.smhrd.teamjo.repository.FoodReviewRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendedMealRepository recommendedMealRepository;

    @Autowired
    private WeightRecordRepository weightRecordRepository;

    @Autowired
    private FoodReviewRepository foodReviewRepository;

    @GetMapping({"/", "/main"})
    public String mainPage(HttpSession session, Model model){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userName", loginUser.getName());
            model.addAttribute("userUid", loginUser.getUid());
            model.addAttribute("userProfileImg", loginUser.getProfile_img());
            model.addAttribute("user", loginUser);

            // ✅ Double 타입 그대로 넘김 (JS에서 숫자 비교 가능)
            model.addAttribute("userRecomCal", loginUser.getRecomCal());
        } else {
            model.addAttribute("isLoggedIn", false);

            // ✅ 비로그인 시도 기본값 0.0
            model.addAttribute("userRecomCal", 0.0);
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

            // 체중 기록
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            List<WeightRecord> records = weightRecordRepository
                    .findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(user.getUid(), oneMonthAgo);
            model.addAttribute("weightRecords", records);

            // 식단 기록
            List<RecommendedMeal> meals = recommendedMealRepository.findByUserId(user.getUid());
            Map<String, List<Map<String, Object>>> mealsByDay = new HashMap<>();

            // 요일 초기화
            for (String day : List.of("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")) {
                mealsByDay.put(day, Arrays.asList(
                        new HashMap<>(), new HashMap<>(), new HashMap<>()
                ));
            }

            for (RecommendedMeal meal : meals) {
                String day = meal.getWeekday();
                int idx = switch (meal.getTime()) {
                    case "morning" -> 0;
                    case "lunch" -> 1;
                    case "dinner" -> 2;
                    default -> -1;
                };

                if (idx != -1) {
                    String name = meal.getRice() + " + " + meal.getSoup() + " + " + meal.getSide();

                    // 각 이미지 가져오기
                    FoodInfo rice = foodRepository.findById(meal.getRice()).orElse(null);
                    FoodInfo soup = foodRepository.findById(meal.getSoup()).orElse(null);
                    FoodInfo side = foodRepository.findById(meal.getSide()).orElse(null);

                    List<String> images = new ArrayList<>();
                    images.add((rice != null && rice.getImg() != null) ? rice.getImg() : "/image/default_rice.png");
                    images.add((soup != null && soup.getImg() != null) ? soup.getImg() : "/image/default_soup.png");
                    images.add((side != null && side.getImg() != null) ? side.getImg() : "/image/default_side.png");

                    Map<String, Object> mealInfo = new HashMap<>();
                    mealInfo.put("name", name);
                    mealInfo.put("images", images);

                    mealsByDay.get(day).set(idx, mealInfo);
                }
            }

            // mealsByDay를 JS에서 JSON으로 쓰기 위해 문자열로 변환
            try {
                String mealsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(mealsByDay);
                model.addAttribute("mealsByDay", mealsJson);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 리뷰 기반 인기 메뉴
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

    @GetMapping("/food-detail/{foodId}")
    public String showFoodDetail(@PathVariable String foodId, Model model) {
        FoodInfo food = foodRepository.findById(foodId).orElseThrow();
        List<FoodReview> reviews = foodReviewRepository.findByFoodId(foodId);

        model.addAttribute("food", food);
        model.addAttribute("reviews", reviews);

        return "food-detail";
    }

    
}

package com.smhrd.teamjo.controller;

import java.io.File;
import java.io.IOException;
import java.lang.StackWalker.Option;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.entity.WeightRecord;
import com.smhrd.teamjo.repository.UserRepository;
import com.smhrd.teamjo.repository.WeightRecordRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    private final UserRepository userRepository;

    // 생성자 주입 방식
    public PageController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // 메인 페이지
    @GetMapping({"/", "/main"})
    public String mainPage(HttpSession session, Model model){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userName", loginUser.getName());
            model.addAttribute("userEmail", loginUser.getEmail());
            model.addAttribute("userProfileImg", loginUser.getProfile_img());
            model.addAttribute("user", loginUser);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "main";
    }

    // 마이페이지 이동
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        // 1. 세션에서 사용자 정보 꺼내기
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/"; // 로그인 안 되어있으면 홈으로
        }

        // 2. 최신 정보가 필요하면 DB에서 조회 (선택 사항)
        Optional<UserInfo> userOpt = userRepository.findById(loginUser.getEmail());
        if (userOpt.isPresent()) {
            UserInfo user = userOpt.get();

            session.setAttribute("loginUser", user);
            // 전체 user 객체를 넘겨줌
            model.addAttribute("user", user);

            // 3. 모델에 필요한 값만 담기
            model.addAttribute("userName", user.getName());
            model.addAttribute("userAge", user.getAge());
            model.addAttribute("userSex", user.getSex());

            // 한 달간 체중 기록 조회
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            List<WeightRecord> records = weightRecordRepository.findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(user.getUid(), oneMonthAgo);
            model.addAttribute("weightRecords", records);
        }

        return "mypage"; // templates/mypage.html
    }

    // 개인정보변경 이동
    @GetMapping("/profile-edit")
    public String profileEditPage(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null){
            return "redirect:/";
        }

        Optional<UserInfo> userOpt = userRepository.findById(loginUser.getEmail());
        if(userOpt.isPresent()){
            UserInfo user = userOpt.get();
            model.addAttribute("nickname", user.getNick());
            model.addAttribute("height", user.getHeight());
            model.addAttribute("weight", user.getWeight());
            model.addAttribute("age", user.getAge());
            model.addAttribute("sex", user.getSex());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }

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
            Optional<UserInfo> userOpt = userRepository.findById(loginUser.getEmail());
            if (userOpt.isPresent()) {
                UserInfo user = userOpt.get();

                user.setNick(nickname);
                user.setHeight(height);
                user.setWeight(weight);
                user.setAge(age);
                user.setSex(sex);

                String uploadDir = "C:/Users/smhrd/Desktop/gitTestJo-1/upload";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()){
                    uploadFolder.mkdirs();
                }

                // 2. 프로필 이미지 처리
                if (profileImage != null && !profileImage.isEmpty()){
                    try{
                        // 파일 저장 경로 설정
                        String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                        File dest = new File(uploadDir + "/" + fileName);
                        profileImage.transferTo(dest);

                        // DB에 경로 저장 (접근 가능한 경로)
                        user.setProfile_img("/upload/" + fileName);

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                
                // DB 저장 및 세션 갱신
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
            Optional<UserInfo> userOpt = userRepository.findById(loginUser.getEmail());
            if (userOpt.isPresent()){
                UserInfo user = userOpt.get();

                // 기본 이미지 경로로 초기화
                user.setProfile_img("/image/default_profile.png");

                userRepository.save(user);
                session.setAttribute("loginUser", user);
            }
        }
        return "redirect:/profile-edit";
    }

    @Autowired
    private WeightRecordRepository weightRecordRepository;

    @PostMapping("/recordWeight")
    public String recordWeight(@RequestParam("weight") double weight, HttpSession session){
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser != null){
            //1. 체중 기록 추가
            WeightRecord newRecord = new WeightRecord();
            newRecord.setUserId(loginUser.getEmail());
            newRecord.setWeight(weight);
            weightRecordRepository.save(newRecord);

            //2. USER_INFO의 현재 체중 업데이트
            loginUser.setWeight(weight);
            userRepository.save(loginUser);
            session.setAttribute("loginUser", loginUser);
        }
        return "redirect:/mypage";
    }

    // 한 달간 체중 불러오기
    @GetMapping("/weight-chart")
    public String showWeightChart(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/";
        }

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<WeightRecord> records = weightRecordRepository
            .findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(loginUser.getUid(), oneMonthAgo);

        model.addAttribute("weightRecords", records);
        return "weight-chart"; // 이 뷰에서 그래프 출력
    }
    
    @GetMapping("/calorie")
    public String showCaloriePage() {
        return "calorie";
    }
    
}

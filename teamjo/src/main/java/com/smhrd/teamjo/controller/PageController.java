package com.smhrd.teamjo.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.UserRepository;

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

            // 3. 모델에 필요한 값만 담기
            model.addAttribute("userName", user.getName());
            model.addAttribute("userAge", user.getAge());
            model.addAttribute("userSex", user.getSex());
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
            model.addAttribute("nickname", user.getName());
        }

        model.addAttribute("user", loginUser);
        return "profile-edit";
    }
}

package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.UserDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.service.UserService;
import com.smhrd.teamjo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // ✅ 중복 확인용

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute UserDTO userDTO, RedirectAttributes rttr) {

        // ✅ 이메일 중복 체크
        if (userRepository.existsByUid(userDTO.getUid())) {
            rttr.addFlashAttribute("registerError", "이미 사용 중인 이메일입니다.");
            return "redirect:/main";
        }

        // 회원가입 로직 실행
        userService.join(userDTO);

        return "redirect:/";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("uid") String uid,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        UserInfo loginUser = userService.login(uid, password);

        if (loginUser != null){
            session.setAttribute("loginUser", loginUser);
            return "redirect:/main";
        } else {
            model.addAttribute("loginError", true);
            return "main";
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/main";
    }
}

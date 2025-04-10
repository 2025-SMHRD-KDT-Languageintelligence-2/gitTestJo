package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.UserDTO;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute UserDTO userDTO){

        //회원가입 로직 실행
        userService.join(userDTO);

        // 회원가입 후 메인페이지로 리다이렉팅
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam("uid") String uid,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        UserInfo loginUser = userService.login(uid, password);

        if (loginUser != null){
            session.setAttribute("loginUser", loginUser);
            return "redirect:/main";
        }
        else{
            model.addAttribute("loginError", true);
            return "main";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/main";
    }

}

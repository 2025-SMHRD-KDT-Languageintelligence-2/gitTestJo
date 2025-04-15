package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.entity.OrderInfo;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderRepository orderRepository;

    // ✅ 주문 내역 페이지 이동
    @GetMapping("/orderHistory")
    public String showOrderHistory(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        // 로그인 상태 확인
        if (loginUser == null) {
            return "redirect:/"; // 로그인 안 되어있을 경우 홈으로
        }

        // 주문 내역 조회
        List<OrderInfo> orderList = orderRepository.findByUserIdOrderByCreatedAtDesc(loginUser.getUid());
        model.addAttribute("orderList", orderList);

        return "order-history"; // → templates/order-history.html
    }
}

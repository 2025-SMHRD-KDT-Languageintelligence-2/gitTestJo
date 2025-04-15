package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.KakaoPayReadyRequest;
import com.smhrd.teamjo.entity.Cart;
import com.smhrd.teamjo.entity.FoodInfo;
import com.smhrd.teamjo.entity.OrderInfo;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.CartRepository;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.repository.OrderRepository;
import com.smhrd.teamjo.service.KakaoPayService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    // ✅ 결제 요청
    @PostMapping("/pay")
    public void kakaoPay(HttpSession session, HttpServletResponse response) throws IOException {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        List<Cart> selectedCarts = (List<Cart>) session.getAttribute("selectedCarts");

        if (loginUser == null || selectedCarts == null) {
            response.sendRedirect("/cart");
            return;
        }

        int totalPrice = 0;
        for (Cart cart : selectedCarts) {
            Optional<FoodInfo> foodOpt = foodRepository.findById(cart.getFoodId());
            if (foodOpt.isPresent()) {
                totalPrice += foodOpt.get().getPrice();
            }
        }

        KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
                .partner_order_id("ORDER_" + System.currentTimeMillis())
                .partner_user_id(loginUser.getUid())
                .item_name("맞춤형 식단 (" + selectedCarts.size() + "개)")
                .quantity(1)
                .total_amount(totalPrice)
                .tax_free_amount(0)
                .build();

        String redirectUrl = kakaoPayService.kakaoPayReady(request);
        response.sendRedirect(redirectUrl);
    }

    // ✅ 결제 성공 시: 주문 저장 + 장바구니 삭제
    @GetMapping("/paySuccess")
    public String paySuccess(Model model, HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        List<Cart> selectedCarts = (List<Cart>) session.getAttribute("selectedCarts");

        if (loginUser == null || selectedCarts == null) {
            return "redirect:/cart";
        }

        for (Cart cart : selectedCarts) {
            // 주문 저장
            OrderInfo order = OrderInfo.builder()
                    .userId(loginUser.getUid())
                    .foodId(cart.getFoodId())
                    .recommendedMealId(cart.getMealId()) // 추후 확장 가능
                    .address(loginUser.getAddress())
                    .name(loginUser.getName())
                    .createdAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order);

            // 장바구니 삭제
            cartRepository.deleteById(cart.getCartId());
        }

        session.removeAttribute("selectedCarts"); // 세션 정리
        model.addAttribute("status", "success");
        return "pay-result";
    }

    // ✅ 결제 취소 시
    @GetMapping("/payCancel")
    public String payCancel(Model model) {
        model.addAttribute("status", "cancel");
        return "pay-result";
    }

    // ✅ 결제 실패 시
    @GetMapping("/payFail")
    public String payFail(Model model) {
        model.addAttribute("status", "fail");
        return "pay-result";
    }
}

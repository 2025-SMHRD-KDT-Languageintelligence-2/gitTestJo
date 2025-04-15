package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.entity.Cart;
import com.smhrd.teamjo.entity.FoodInfo;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.CartRepository;
import com.smhrd.teamjo.repository.FoodRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;

    // ✅ 결제 페이지 이동 (선택된 상품 정보 전달)
    @PostMapping("/checkout")
    public String goToCheckout(@RequestParam(name = "selectedCartIds", required = false) List<Long> selectedCartIds,
                               HttpSession session,
                               Model model) {

        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/main";
        }

        if (selectedCartIds == null || selectedCartIds.isEmpty()) {
            model.addAttribute("error", "결제할 상품을 선택해주세요.");
            return "redirect:/cart";
        }

        List<Map<String, Object>> selectedItems = new ArrayList<>();
        int totalPrice = 0;
        double totalCalories = 0.0;

        for (Long cartId : selectedCartIds) {
            Optional<Cart> cartOpt = cartRepository.findById(cartId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                Optional<FoodInfo> foodOpt = foodRepository.findById(cart.getFoodId());
                if (foodOpt.isPresent()) {
                    FoodInfo food = foodOpt.get();

                    Map<String, Object> item = new HashMap<>();
                    item.put("name", food.getName());
                    item.put("price", food.getPrice());
                    item.put("calorie", food.getEnergy());
                    item.put("type", food.getType());

                    totalPrice += food.getPrice();
                    totalCalories += food.getEnergy();

                    selectedItems.add(item);
                }
            }
        }

        // 장바구니 상품 정보
        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalCalories", totalCalories);

        // 사용자 정보 → 자동 입력용
        model.addAttribute("userName", loginUser.getName());
        model.addAttribute("userPhone", loginUser.getPhone());
        model.addAttribute("userAddress", loginUser.getAddress()); // 주소 컬럼 존재 시

        return "checkout"; // → templates/checkout.html
    }

    
}

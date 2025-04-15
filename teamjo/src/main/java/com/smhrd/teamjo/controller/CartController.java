package com.smhrd.teamjo.controller;

import com.smhrd.teamjo.dto.CartRequestDTO;
import com.smhrd.teamjo.entity.Cart;
import com.smhrd.teamjo.entity.UserInfo;
import com.smhrd.teamjo.repository.CartRepository;
import com.smhrd.teamjo.repository.FoodRepository;
import com.smhrd.teamjo.entity.FoodInfo;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller // ✅ RestController → Controller로 수정 (HTML 렌더링을 위해)
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;

    // ✅ 장바구니에 추가
    @PostMapping("/add")
    @ResponseBody // AJAX 요청을 위한 응답 문자열 처리
    public String addToCart(@RequestBody CartRequestDTO request, HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "로그인이 필요합니다.";
        }

        String userId = loginUser.getUid();

        Cart cartItem = new Cart();
        cartItem.setUserId(userId);
        cartItem.setFoodId(request.getFoodId());
        cartItem.setMealId(request.getMealId());
        cartItem.setCreatedAt(LocalDateTime.now());

        cartRepository.save(cartItem);
        return "장바구니에 추가되었습니다.";
    }

    // ✅ 장바구니 페이지 렌더링
    @GetMapping
    public String showCart(HttpSession session, Model model) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/main";
        }

        String userId = loginUser.getUid();
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        List<Map<String, Object>> cartView = new ArrayList<>();
        int totalPrice = 0;
        double totalCalories = 0;

        for (Cart cart : cartItems) {
            Optional<FoodInfo> foodOpt = foodRepository.findById(cart.getFoodId());
            if (foodOpt.isPresent()) {
                FoodInfo food = foodOpt.get();

                Map<String, Object> item = new HashMap<>();
                item.put("name", food.getName());
                item.put("price", food.getPrice());
                item.put("calorie", food.getEnergy());
                item.put("foodId", food.getFoodId());
                item.put("cartId", cart.getCartId());
                item.put("type", food.getType());

                totalPrice += food.getPrice();
                totalCalories += food.getEnergy();

                cartView.add(item);
            }
        }

        model.addAttribute("cartItems", cartView);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalCalories", totalCalories);

        return "cart"; // → cart.html 렌더링
    }

    @PostMapping("/delete")
    public String deleteCartItem(@RequestParam("cartId") Long cartId, HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 로그인된 사용자에 해당하는 cartId만 삭제
        cartRepository.findById(cartId).ifPresent(cart -> {
            if (cart.getUserId().equals(loginUser.getUid())) {
                cartRepository.deleteById(cartId);
            }
        });

        return "redirect:/cart"; // 삭제 후 장바구니 페이지로 리다이렉트
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        UserInfo loginUser = (UserInfo) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        String userId = loginUser.getUid();
        cartRepository.deleteByUserId(userId);

        return "redirect:/cart";
    }
}

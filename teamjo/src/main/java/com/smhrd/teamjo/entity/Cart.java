package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CART")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long cartId;

    @Column(name = "U_ID")
    private String userId;

    @Column(name = "FOOD_ID")
    private String foodId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "MEAL_ID")
    private Long mealId;  // RecommendedMeal의 ID
}

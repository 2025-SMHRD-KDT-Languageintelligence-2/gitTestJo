package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDER_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_IDX")
    private Long orderIdx;

    @Column(name = "U_ID", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOOD_ID", insertable = false, updatable = false)
    private FoodInfo food;

    @Column(name = "FOOD_ID")
    private String foodId;

    @Column(name = "ID")
    private Long recommendedMealId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "NAME")
    private String name;
}

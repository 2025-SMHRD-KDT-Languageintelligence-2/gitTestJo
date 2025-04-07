package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SIDE_PREFERENCE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SidePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "U_ID", nullable = false)
    private String userId;

    @Column(name = "SIDE_TYPE")
    private String sideType;

    @Column(name = "SIDE_FLAVOR")
    private String sideFlavor;

    @Column(name = "EXCLUDE_INGREDIENTS")
    private String excludeIngredients;

    @Column(name = "AVOID_TYPE")
    private String avoidType;

    @Column(name = "COOKING_METHOD")
    private String cookingMethod;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}

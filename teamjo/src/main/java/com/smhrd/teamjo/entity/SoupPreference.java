package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SOUP_PREFERENCE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SoupPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "U_ID", nullable = false)
    private String userId;

    @Column(name = "SOUP_TYPE")
    private String soupType;

    @Column(name = "SPICY_LEVEL")
    private String spicyLevel;

    @Column(name = "SOUP_AMOUNT")
    private String soupAmount;

    @Column(name = "EXCLUDE_INGREDIENTS")
    private String excludeIngredients;

    @Column(name = "DIET_SOUP_PREF")
    private String dietSoupPref;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RICE_PREFERENCE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RicePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "U_ID", nullable = false)
    private String userId;

    @Column(name = "RICE_TYPE")
    private String riceType;

    @Column(name = "STICKINESS")
    private String stickiness;

    @Column(name = "GRAINS")
    private Boolean grains;

    @Column(name = "EXCLUDE_INGREDIENTS")
    private String excludeIngredients;

    @Column(name = "DIET_RICE_PREF")
    private String dietRicePref;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "FOOD_REVIEW")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_IDX")
    private int reviewIdx;

    @Column(name = "U_ID")
    private String uid;  // 작성자 ID

    @Column(name = "FOOD_ID")
    private String foodId;  // 음식 ID

    @Column(name = "F_REVIEW")
    private String fReview; // 리뷰 내용

    @Column(name = "F_SCORE")
    private int fScore;     // 리뷰 점수

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}

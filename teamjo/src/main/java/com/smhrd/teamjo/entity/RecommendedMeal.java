package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RECOMMENDED_MEAL") // 테이블명 대문자
public class RecommendedMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private String userId; // USER_INFO 테이블의 U_ID와 연결

    @Column(name = "TIME", nullable = false)
    private String time;   // morning, lunch, evening

    @Column(name = "RICE")
    private String rice;

    @Column(name = "SOUP")
    private String soup;

    @Column(name = "SIDE")
    private String side;

    @Column(name = "TOTAL_CALORIES")
    private Double totalCalories;

    @Column(name = "MEAL_DATE", nullable = false)
    private LocalDate mealDate; // 날짜

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "WEEKDAY")
    private String weekday;

    @Column(name = "SIMILARITY")
    private Double similarity;
}

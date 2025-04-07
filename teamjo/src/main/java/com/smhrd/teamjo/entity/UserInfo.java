package com.smhrd.teamjo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "\"USER_INFO\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    @Column(name = "U_ID", length = 50)
    private String uid;  // 고유 사용자 ID (이메일 기반)

    // 🔐 로그인 관련
    @Column(name = "U_EMAIL", length = 50, nullable = false)
    private String email;

    @Column(name = "U_PW", length = 50, nullable = false)
    private String password;

    @Column(name = "U_ROLE", length = 10)
    private String role;

    @Column(name = "LOGIN_SRC", length = 10)
    private String loginSrc;

    @Column(name = "JOINED_AT")
    private Timestamp joinedAt;

    // 👤 사용자 기본 정보
    @Column(name = "U_NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "U_PHONE", length = 20, nullable = false)
    private String phone;

    @Column(name = "U_NICK")
    private String nick;

    @Column(name = "U_AGE")
    private int age;

    @Column(name = "U_SEX")
    private String sex;

    // 📏 신체 정보
    @Column(name = "U_HEIGHT")
    private Double height;

    @Column(name = "U_WEIGHT")
    private Double weight;

    // 📷 프로필 이미지
    @Column(name = "U_IMG1")
    private String profile_img;

    // 🔥 권장 섭취 칼로리
    @Column(name = "RECOM_CAL")
    private Integer recomCal;

    // 🍽 식사 관련 정보
    @Column(name = "U_MEAL_COUNT")
    private Integer mealCount;  // 하루 식사 횟수

    @Column(name = "U_MEAL_TIMES")
    private String mealTimes;   // 예: "아침,점심,저녁"
}

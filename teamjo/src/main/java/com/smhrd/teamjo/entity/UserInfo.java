// src/main/java/com/smhrd/teamjo/entity/UserInfo.java

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
    private String uid;  // 이메일이나 직접 생성된 ID

    @Column(name = "U_PW", length = 50, nullable = false)
    private String password;

    @Column(name = "U_EMAIL", length = 50, nullable = false)
    private String email;

    @Column(name = "U_NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "U_PHONE", length = 20, nullable = false)
    private String phone;

    @Column(name = "U_HEIGHT")
    private Double height;

    @Column(name = "U_WEIGHT")
    private Double weight;

    @Column(name = "U_ROLE", length = 10)
    private String role;

    @Column(name = "JOINED_AT")
    private Timestamp joinedAt;

    @Column(name = "LOGIN_SRC", length = 10)
    private String loginSrc;

    @Column(name = "U_SEX")
    private String sex;

    @Column(name = "U_AGE")
    private int age;

    @Column(name = "U_IMG1")
    private String profile_img;
}

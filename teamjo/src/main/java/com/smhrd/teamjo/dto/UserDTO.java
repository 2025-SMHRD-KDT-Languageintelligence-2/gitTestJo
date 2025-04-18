package com.smhrd.teamjo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String uid;
    private String password;
    private String name;
    private String phone;
    private Integer recomcal;
}

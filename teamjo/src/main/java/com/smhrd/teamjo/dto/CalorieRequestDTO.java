package com.smhrd.teamjo.dto;

import lombok.Data;

@Data
public class CalorieRequestDTO {
    private int hight;
    private int currentWeight;
    private int goalWeight;
    private int year;
    private int month;
    private int day;
    private int duration;
    private String unit;
    private int activity;
    private String gender;
}

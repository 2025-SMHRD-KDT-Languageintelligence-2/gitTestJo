package com.smhrd.teamjo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealCombination {
    private String rice;
    private String soup;
    private String side;
    private double totalCalories;
} 

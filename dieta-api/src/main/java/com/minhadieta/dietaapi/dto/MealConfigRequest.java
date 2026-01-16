package com.minhadieta.dietaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealConfigRequest {
    private String name;
    private String time;
    private Double targetCalories;
}

package com.quantumblink;

public class FoodType {
    private final int Food_ID;
    private final String Food_name;
    private final int calories;
    private final int fat;
    private final int protein;

    public FoodType(int food_ID, String food_name, int calories, int fat, int protein, int carbs) {
        Food_ID = food_ID;
        Food_name = food_name;
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
        this.carbs = carbs;
    }

    public int getFood_ID() {
        return Food_ID;
    }

    public String getFood_name() {
        return Food_name;
    }

    public int getCalories() {
        return calories;
    }

    public int getFat() {
        return fat;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    private final int carbs;
}

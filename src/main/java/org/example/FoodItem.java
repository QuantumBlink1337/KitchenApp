package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FoodItem {
    private final int ID;
    static char ID_Type = 'F';
    private final String name;
    private final float cost;
    private final float mass;
    private final LocalDate purchaseDate;
    private final LocalDate bestbyDate;
    private final int age;
    private final int daysPastSpoil;
    private final int cal;
    private final int protein;

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public float getMass() {
        return mass;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getBestbyDate() {
        return bestbyDate;
    }

    public int getAge() {
        return age;
    }

    public int getDaysPastSpoil() {
        return daysPastSpoil;
    }

    public int getCal() {
        return cal;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    private int fat;
    private final int carbs;


    public FoodItem(int id, String foodName, float cost, float mass, LocalDate purchaseDate,
                    LocalDate bestbyDate, int age, int daysPastSpoil, int cal, int protein, int fat, int carbs) {
        ID = id;
        name = foodName;
        this.cost = cost;
        this.mass = mass;
        this.purchaseDate = purchaseDate;
        this.bestbyDate = bestbyDate;
        this.age = age;
        this.daysPastSpoil = daysPastSpoil;
        this.cal = cal;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    public int getCarbs() {
        return carbs;
    }
}

package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 22.10.2017.
 */

public class ItemRates {

    private String nameRate;
    private double courseRate;

    public ItemRates(String nameRate, double courseRate) {
        this.nameRate = nameRate;
        this.courseRate = courseRate;
    }

    public String getNameRate() {
        return nameRate;
    }

    public double getCourseRate() {
        return courseRate;
    }
}

package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 22.07.2017.
 */

public class ItemDataWithDB {
    private String getName;
    private String howMach;

    public ItemDataWithDB(String getName, String howMach) {
        this.getName = getName;
        this.howMach = howMach;
    }

    public String getGetName() {
        return getName;
    }

    public String getHowMach() {
        return howMach;
    }
}

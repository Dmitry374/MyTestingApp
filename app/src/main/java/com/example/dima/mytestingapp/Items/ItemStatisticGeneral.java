package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 15.10.2017.
 */

public class ItemStatisticGeneral {

    private String date;
    private String onCount;
    private String getName;
    private String spendName;
    private int color;
    private int id;

    public ItemStatisticGeneral(String _date, String _onCount, String _getName, String _spendName,
                                int _color, int _id) {
        date = _date;
        onCount = _onCount;
        getName = _getName;
        spendName = _spendName;
        color = _color;
        id = _id;
    }

    public String getDate() {
        return date;
    }

    public String getOnCount() {
        return onCount;
    }

    public String getGetName() {
        return getName;
    }

    public String getSpendName() {
        return spendName;
    }

    public int getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}

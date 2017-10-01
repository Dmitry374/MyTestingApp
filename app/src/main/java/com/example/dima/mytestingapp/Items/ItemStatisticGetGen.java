package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 03.08.2017.
 */

public class ItemStatisticGetGen {

    private String date;
    private String onCount;
    private String getName;

    public ItemStatisticGetGen(String _date, String _onCount, String _getName) {
        date = _date;
        onCount = _onCount;
        getName = _getName;
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
}
